package com.example.docconneting.common.principal;

import io.opencensus.internal.DefaultVisibilityForTesting;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final String name;

    public StompPrincipal(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
