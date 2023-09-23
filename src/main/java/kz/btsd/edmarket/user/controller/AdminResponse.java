package kz.btsd.edmarket.user.controller;

import lombok.Data;

@Data
public class AdminResponse {
    private boolean superAdmin=false;

    public AdminResponse(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}
