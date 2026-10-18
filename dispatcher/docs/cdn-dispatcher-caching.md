# CDN / Dispatcher Caching — AEM as a Cloud Service

## Architecture

```
Browser → CDN (Fastly, managed via Cloud Manager) → Dispatcher (Apache module) → Publish (AEM)
```

Two **independent cache layers** sit in front of AEM:

| Layer | Managed by | Config location |
|---|---|---|
| CDN | Adobe (Cloud Manager) | Cloud Manager CDN rule sets (not in repo) + response headers |
| Dispatcher | You (project repo) | `dispatcher/src/conf.dispatcher.d/cache/*.any` |

A URL can be cached at the CDN but *not* at the Dispatcher, or vice versa — each layer makes its own caching decision.

---

## 1. How to enable CDN caching

- The CDN is **enabled by default** on every AEMaaCS Author/Publish tier (Fastly-backed) — there's no on/off toggle, it fronts all traffic automatically.
- What you configure is **cache behavior**, via standard HTTP headers emitted by AEM/Dispatcher:
  - `Cache-Control: max-age=<seconds>`
  - `Surrogate-Control`
- For advanced edge logic (custom rules, header rewriting, redirects), opt in to **CDN rule sets** via Cloud Manager — deployed independently of the dispatcher pipeline.
- Project reference: `dispatcher/src/conf.dispatcher.d/clientheaders/clientheaders.any` controls which headers from AEM are passed through the Dispatcher (and therefore visible to the CDN).

## 2. How to access / inspect CDN cache

No UI to "browse" CDN contents — infer state from response headers:

```bash
curl -I https://your-site.com/en/home.html
```

Look for:
- `X-Cache: HIT` / `MISS`
- `X-Served-By` — Fastly POP identifier
- `Age` — seconds since the object was cached at the edge

## 3. Request flow: CDN → Dispatcher → AEM

1. Request hits nearest CDN edge (Fastly POP).
2. CDN checks its cache using headers from the previous response for that URL.
3. **On CDN miss** → forwarded to Dispatcher (origin) over Adobe's private network.
   - Entry point: `dispatcher/src/conf.d/available_vhosts/default.vhost`
4. Dispatcher checks its own file-system cache per:
   - `dispatcher/src/conf.dispatcher.d/cache/rules.any` → includes `default_rules.any`
5. **On Dispatcher miss** → proxied to AEM Publish per `renders/default_renders.any`.
6. Response flows back up: AEM → Dispatcher (cached per rules) → CDN (cached per headers) → Browser.

## 4. How to bypass CDN caching

- CDN caching decisions are based on **origin response headers**, not client request headers. To bypass:
  - Origin (AEM/servlet/HTL) must emit `Cache-Control: no-store` or `Cache-Control: private`.
  - Requests carrying session cookies / `Authorization` headers are not cached by default (personalized content).
- **Permanent bypass for a path** — deny it at the Dispatcher level too, so neither layer caches it. Existing example in this repo:

```any
# dispatcher/src/conf.dispatcher.d/cache/default_rules.any
/0001 {
    /glob "/libs/granite/csrf/token.json"
    /type "deny"
}
```

Add a similar `/glob` + `/type "deny"` entry in `rules.any` for any dynamic/user-specific endpoint.

## 5. CDN cache invalidation & expiration

**Expiration (TTL) — passive:**
- Driven entirely by `Cache-Control: max-age` / `Surrogate-Control` headers set at origin. CDN auto-expires entries — no manual action required.

**Invalidation (flush) — active:**
- **Dispatcher cache**: flushed via replication from AEM backend. Only allowed IPs may trigger it:

```any
# dispatcher/src/conf.dispatcher.d/cache/default_invalidate.any
/0001 {
    /type "deny"
    /glob "*.*.*.*"
}
/0002 {
    /type "allow"
    /glob "${AEM_IP}"
}
```

- **CDN cache**: Adobe automatically propagates invalidation as part of the publish/activation pipeline — no customer-managed flush agent required. For custom CDN rule-set setups, Cloud Manager exposes purge APIs for manual invalidation.

---

## Key takeaway for teaching

> CDN and Dispatcher are two separate caches with two separate rule engines — one driven by HTTP headers (CDN), one driven by glob-pattern `.any` config files (Dispatcher). Always reason about them independently when debugging stale or missing cache behavior.
