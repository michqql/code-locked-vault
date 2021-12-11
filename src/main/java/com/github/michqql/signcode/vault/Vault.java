package com.github.michqql.signcode.vault;

import java.util.UUID;

public class Vault {

    private UUID owner;
    private Code code;

    public Vault(UUID owner) {
        this.owner = owner;
        this.code = new Code();
    }

    public UUID getOwner() {
        return owner;
    }

    public Code getCode() {
        return code;
    }
}
