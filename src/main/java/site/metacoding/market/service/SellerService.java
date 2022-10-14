package site.metacoding.market.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.metacoding.market.domain.user.seller.SellerRepository;

@RequiredArgsConstructor
@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerRepository getRepo() {
        return sellerRepository;
    }
}
