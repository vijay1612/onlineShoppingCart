package com.gmail.vijay.ecommerce.service.impl;

import com.gmail.vijay.ecommerce.constants.ErrorMessage;
import com.gmail.vijay.ecommerce.constants.SuccessMessage;
import com.gmail.vijay.ecommerce.domain.Order;
import com.gmail.vijay.ecommerce.domain.User;
import com.gmail.vijay.ecommerce.dto.request.ChangePasswordRequest;
import com.gmail.vijay.ecommerce.dto.request.EditUserRequest;
import com.gmail.vijay.ecommerce.dto.request.SearchRequest;
import com.gmail.vijay.ecommerce.dto.response.MessageResponse;
import com.gmail.vijay.ecommerce.repository.OrderRepository;
import com.gmail.vijay.ecommerce.repository.UserRepository;
import com.gmail.vijay.ecommerce.security.UserPrincipal;
import com.gmail.vijay.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getAuthenticatedUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(principal.getUsername());
    }

    @Override
    public Page<Order> searchUserOrders(SearchRequest request, Pageable pageable) {
        User user = getAuthenticatedUser();
        return orderRepository.searchUserOrders(user.getId(), request.getSearchType(), request.getText(), pageable);
    }

    @Override
    @Transactional
    public MessageResponse editUserInfo(EditUserRequest request) {
        User user = getAuthenticatedUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPostIndex(request.getPostIndex());
        return new MessageResponse("alert-success", SuccessMessage.USER_UPDATED);
    }

    @Override
    @Transactional
    public MessageResponse changePassword(ChangePasswordRequest request) {
        if (request.getPassword() != null && !request.getPassword().equals(request.getPassword2())) {
            return new MessageResponse("passwordError", ErrorMessage.PASSWORDS_DO_NOT_MATCH);
        }
        User user = getAuthenticatedUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return new MessageResponse("alert-success", SuccessMessage.PASSWORD_CHANGED);
    }
}
