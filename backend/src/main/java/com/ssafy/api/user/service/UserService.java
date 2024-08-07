package com.ssafy.api.user.service;

import com.ssafy.api.user.request.UserRegisterPostReq;
import com.ssafy.api.user.request.UserUpdatePostReq;
import com.ssafy.db.entity.User;

import javax.mail.MessagingException;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface UserService {
	User createUser(UserRegisterPostReq userRegisterInfo);

	User getUserByEmail(String email);

	User getUserByEmailId(String emailId);

	User getUserByEmailIdAndEmailDomain(String emailId, String emailDomain);

	void loginSaveJwt(String userId, String jwtToken);

	void logoutSaveJwt(String userId);

	User updateUser(User user, UserUpdatePostReq userUpdateInfo);

	void deleteUser(User user);

	boolean idDupCheck(String userId);

	boolean checkPassword(String password, User user);

	void updatePassword(String password, User user);

	boolean nicknameDupCheck(String nickName);

	String generateRandomNickname();

	boolean checkEmailAuthToken(Long userIdx);

	void saveEmailAuthToken(Long userIdx, String token);

	void sendEmail(Long userIdx, String token) throws MessagingException;

	boolean verifyEmail(Long userIdx, String token);
}
