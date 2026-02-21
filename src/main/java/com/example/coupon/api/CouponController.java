// src/main/java/com/example/coupon/api/CouponController.java
package com.example.coupon.api;

import com.example.coupon.service.CouponIssueService;
import com.example.coupon.service.IssueResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponIssueService service;

    public CouponController(CouponIssueService service) {
        this.service = service;
    }

    public record IssueRequest(Long userId) {}
    public record IssueResponse(String code) {}

    @PostMapping("/{couponId}/issue")
    public ResponseEntity<IssueResponse> issue(@PathVariable Long couponId,
                                               @RequestBody IssueRequest req) {
        IssueResult result = service.issue(couponId, req.userId());

        return switch (result) {
            case SUCCESS -> ResponseEntity.ok(new IssueResponse("SUCCESS"));
            case SOLD_OUT -> ResponseEntity.status(409).body(new IssueResponse("SOLD_OUT"));
            case DUPLICATE -> ResponseEntity.status(409).body(new IssueResponse("DUPLICATE"));
        };
    }
}
