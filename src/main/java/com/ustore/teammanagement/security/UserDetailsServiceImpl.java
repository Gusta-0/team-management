package com.ustore.teammanagement.security;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Membro não encontrado: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(member.getUsername())
                .password(member.getPassword())
                .build();
    }
}