# Lucene Indexing in AEM — Hands-on Lab

This lab is built around a real custom Oak Lucene index added to this
project: `/oak:index/ecmPageCategoryLucene-1`
(`ui.content/src/main/content/jcr_root/_oak_index/ecmPageCategoryLucene-1/.content.xml`).

## 1. The problem: a query without a matching index

AEM content queries run on **Oak** (the repository engine), not a SQL
database. When a query filters on a property that has no matching index,
Oak falls back to a **traversal** — walking every node under the search
root and checking each one. This works, but doesn't scale.

**Reproduce it:**

1. Create a test page with a custom property `pageCategory`:
   ```bash
   curl -u admin:admin -F "jcr:primaryType=cq:Page" \
     http://localhost:4502/content/ecm/lucene-lab
   curl -u admin:admin \
     -F "jcr:primaryType=cq:PageContent" \
     -F "pageCategory=campaign-launch" \
     -F "jcr:title=Lucene Lab Test Page" \
     http://localhost:4502/content/ecm/lucene-lab/jcr:content
   ```
2. Query for it via QueryBuilder:
   ```bash
   curl -u admin:admin \
     "http://localhost:4502/bin/querybuilder.json?path=/content&property=pageCategory&property.value=campaign-launch&p.limit=-1"
   ```
   The query **succeeds** (`"success":true`) — but check
   `crx-quickstart/logs/error.log`:
   ```
   WARN ... org.apache.jackrabbit.oak.query.QueryImpl
   Traversal query (query without index): select [jcr:path], [jcr:score], *
   from [nt:base] as a where [pageCategory] = 'campaign-launch'
   and isdescendantnode(a, '/content') ... consider creating an index
   Execution plan: [nt:base] as [a] /* traverse
       allDescendents: /content
       estimatedEntries: 2148.0 */
   ```
   **Teaching point:** correctness and performance are independent — a
   query can return the right answer today and still be a ticking time
   bomb as content volume grows, because it scans the whole subtree.

## 2. The fix: define a Lucene property index

Index definitions live under `/oak:index` and are deployed like any other
content, via a FileVault package. In this project:

`ui.content/src/main/content/jcr_root/_oak_index/ecmPageCategoryLucene-1/.content.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0"
    jcr:primaryType="oak:QueryIndexDefinition"
    compatVersion="{Long}2"
    type="lucene"
    async="async"
    tags="[ecmPageCategory]"
    includedPaths="[/content/ecm]">
    <indexRules jcr:primaryType="nt:unstructured">
        <cq:PageContent jcr:primaryType="nt:unstructured">
            <properties jcr:primaryType="nt:unstructured">
                <pageCategory
                    jcr:primaryType="nt:unstructured"
                    name="pageCategory"
                    propertyIndex="{Boolean}true"
                    notNullCheckEnabled="{Boolean}true"/>
            </properties>
        </cq:PageContent>
    </indexRules>
</jcr:root>
```

Key properties explained:
- `type="lucene"` — a Lucene-backed index (vs `type="property"`, a simpler
  ordered-property index with fewer capabilities).
- `async="async"` — indexed in the background on a dedicated async lane,
  not synchronously on every commit (keeps authoring fast).
- `includedPaths` — restricts the index (and matching queries) to a
  subtree; keeps the index small and relevant.
- `indexRules/cq:PageContent/properties/pageCategory` — declares that
  **only `cq:PageContent` nodes** are indexed for this property. Queries
  against a different node type selector won't match this rule.

### Deploying index definitions via Maven/FileVault

Oak index definitions are flagged specially by the `filevault-package-maven-plugin`
validator. You must explicitly allow them (`ui.content/pom.xml`):
```xml
<plugin>
    <groupId>org.apache.jackrabbit</groupId>
    <artifactId>filevault-package-maven-plugin</artifactId>
    <configuration>
        <allowIndexDefinitions>true</allowIndexDefinitions>
        <validatorsSettings>
            <jackrabbit-filter>
                <options>
                    <validRoots>/conf,/content,/content/experience-fragments,/content/dam,/oak:index</validRoots>
                </options>
            </jackrabbit-filter>
        </validatorsSettings>
        ...
    </configuration>
</plugin>
```
And add the filter root in `META-INF/vault/filter.xml`:
```xml
<filter root="/oak:index/ecmPageCategoryLucene-1" mode="merge"/>
```

Deploy with:
```bash
mvn -pl ui.content -am clean install -PautoInstallPackage
```

## 3. Observing the async reindex

On deploy, Oak's async indexing lane (runs every few seconds) detects the
new index node and performs an initial full reindex:
```
INFO ... IndexUpdate Found a new index node [ecmPageCategoryLucene-1]. Reindexing is requested
INFO ... IndexUpdate Reindexing will be performed for following indexes: [/oak:index/ecmPageCategoryLucene-1]
INFO ... IndexUpdate Indexing report
    - /oak:index/ecmPageCategoryLucene-1*(1)
INFO ... AsyncIndexUpdate [async] Reindexing completed for indexes: [...] in PT0.556S (556 ms)
```
Verify the index node and reindex status directly:
```bash
curl -u admin:admin "http://localhost:4502/oak:index/ecmPageCategoryLucene-1.json"
```

## 4. Proving the index is used — and a subtle gotcha

Re-run the same query, but this time restrict it to the node type declared
in the index's `indexRules` (`cq:PageContent`):
```bash
curl -u admin:admin \
  "http://localhost:4502/bin/querybuilder.json?path=/content/ecm&type=cq:PageContent&property=pageCategory&property.value=campaign-launch&p.limit=-1"
```
Check the log again — **no traversal warning this time.**

Compare with the *same* query but without `type=cq:PageContent` (implicit
`nt:base`):
```bash
curl -u admin:admin \
  "http://localhost:4502/bin/querybuilder.json?path=/content/ecm&property=pageCategory&property.value=campaign-launch&p.limit=-1"
```
This one **still traverses**, even with the index deployed.

**Teaching point (the gotcha):** an index's `indexRules` are scoped to
specific node types. If your query's selector type doesn't match any
declared `indexRules` node type, Oak's query planner won't consider the
index a viable candidate and falls back to traversal — even though the
index technically contains the data. Always query with the most specific
node type that matches your index definition, or declare the index rule
against a broader/common supertype if multiple node types need to match.

## 5. Key takeaways for the session

1. Every custom query property needs an explicit Lucene (or property)
   index — Oak does not auto-index arbitrary properties.
2. Index definitions are deployed as content (`/oak:index/...`), via the
   standard content package pipeline — no special installer step.
3. `async="async"` indexing means there's a short delay between content
   change and index availability — don't assume real-time consistency.
4. The `error.log` `Traversal query (query without index)` warning is
   your primary signal doing this work manually.
5. `indexRules` node-type scoping matters — match your query's node type
   selector to what the index actually declares, or it silently won't be
   picked.
