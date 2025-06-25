package org.ClientApi.DTOs;

public class BaseEntity {

    public static class ErrorDTO {
        public String error;

        public ErrorDTO(String error) {
            this.error = error;
        }
    }

}