package com.aem.ecm.core.services;

public interface TaxService {

    double calculateTax(double price);

    double getTaxPercentage();
}