package webshop.customers;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class ServiceConfiguration extends Configuration {

    private int defaultCreditRating = 3;

    @JsonProperty
    public void setDefaultCategoryId(int rating) {
        this.defaultCreditRating = rating;
    }

    @JsonProperty
    public int getDefaultCreditRating() {
        return this.defaultCreditRating;
    }
}
