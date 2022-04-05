package Couponify.BeckEnd.Couponify.payload;

import lombok.Data;

public class SmsPojo {
    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
