package org.boot.reservationproject.global;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.customer.user.entity.CustomerEntity;
import org.boot.reservationproject.domain.customer.user.repository.CustomerRepository;
import org.boot.reservationproject.domain.seller.manager.entity.SellerEntity;
import org.boot.reservationproject.domain.seller.manager.repository.SellerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
      return customer;
    }

    throw new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.");
  }
}
