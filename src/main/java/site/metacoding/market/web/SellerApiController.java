package site.metacoding.market.web;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.market.service.SellerService;
import site.metacoding.market.web.dto.SellerBaseDto;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SellerApiController extends SellerBaseDto {

    private final SellerService sellerService;

    @PostMapping("/seller")
    public void sellerSave(SellerReqPost SellerReqPost) {

    }

    @GetMapping("/seller")
    public List<SellerResp> getSellerList() {
        return sellerService.getRepo().findAll()
                .stream()
                .map(SellerResp::create)
                .collect(Collectors.toList());
    }

}
