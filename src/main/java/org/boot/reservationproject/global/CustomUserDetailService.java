package org.boot.reservationproject.global;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.entity.CustomerEntity;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.domain.seller.entity.SellerEntity;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
  private final SellerRepository sellerRepository;
  private final CustomerRepository customerRepository;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SellerEntity seller = sellerRepository.findByCpEmail(username).orElse(null);
    if (seller != null) {
      return seller;
    }

    CustomerEntity customer = customerRepository.findByEmail(username).orElse(null);
    if (customer != null) {
      log.info("유저 권한 : "+customer.getAuthorities());
      return customer;
    }

    throw new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.");
  }
}
