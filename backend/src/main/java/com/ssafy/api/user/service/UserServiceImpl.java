package com.ssafy.api.user.service;

import com.ssafy.api.user.request.UserRegisterPostReq;
import com.ssafy.api.user.request.UserUpdatePostReq;
import com.ssafy.common.util.CSVParser;
import com.ssafy.common.util.JwtTokenUtil;
import com.ssafy.db.entity.EmailAuth;
import com.ssafy.db.entity.User;
import com.ssafy.db.repository.EmailAuthRepository;
import com.ssafy.db.repository.EmailAuthRepositorySupport;
import com.ssafy.db.repository.UserRepository;
import com.ssafy.db.repository.UserRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;

	@Autowired
    UserRepositorySupport userRepositorySupport;

	@Autowired
	EmailAuthRepository emailAuthRepository;

	@Autowired
	EmailAuthRepositorySupport emailAuthRepositorySupport;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	Random random = new Random();
	CSVParser frontWords = new CSVParser("front_words");
	CSVParser backWords = new CSVParser("back_words");

	@Override
	public User createUser(UserRegisterPostReq userRegisterInfo) {
		User user = new User();
		user.setEmailId(userRegisterInfo.getEmailId());
		user.setEmailDomain(userRegisterInfo.getEmailDomain());
		// 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
		user.setPassword(passwordEncoder.encode(userRegisterInfo.getPassword()));
		user.setName(userRegisterInfo.getName());
		user.setNickname(userRegisterInfo.getNickname());
		user.setGender(userRegisterInfo.getGender());
		user.setBirth(userRegisterInfo.getBirth());

		// boolean은 isXXX으로 getter 만들어짐!!
		user.setSearchAllow(userRegisterInfo.isSearchAllow());

		return userRepository.save(user);
	}

	@Override
	public User getUserByEmail(String email) {
		String emailId = email.substring(0, email.indexOf('@'));
		String emailDomain = email.substring(email.indexOf('@')+1);
		User user = userRepository.findByEmailIdAndEmailDomain(emailId, emailDomain).get();
		return user;
	}

	@Override
	public User getUserByEmailId(String emailId) {
		User user = userRepository.findByEmailId(emailId).get();
		return user;
	}


	@Override
	public User getUserByEmailIdAndEmailDomain(String emailId, String emailDomain) {
		User user = userRepository.findByEmailIdAndEmailDomain(emailId, emailDomain).get();
		return user;
	}

	@Override
	public void loginSaveJwt(String userId, String jwtToken){
		User user = userRepository.findByEmailId(userId).get();
		user.setJwtToken(jwtToken);
		userRepository.save(user);
	}

	@Override
	public void logoutSaveJwt(String email) {
		String emailId = email.substring(0, email.indexOf('@'));
		User user = userRepository.findByEmailId(emailId).get();
		user.setJwtToken(null);
		userRepository.save(user);
	}

	@Override
	public User updateUser(User user, UserUpdatePostReq userUpdateInfo) {
		// 수정 사항 추가 필요!!
		user.setName(userUpdateInfo.getName());
		user.setNickname(userUpdateInfo.getNickname());
		user.setBirth(userUpdateInfo.getBirth());
		user.setGender(userUpdateInfo.getGender());
		user.setSearchAllow(userUpdateInfo.isSearchAllow());

		System.out.println(userUpdateInfo.isSearchAllow());

		return userRepository.save(user);
	}

	@Override
	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	@Override
	public boolean idDupCheck(String emailId) {
		if (userRepository.findByEmailId(emailId).isPresent()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean checkPassword(String password, User user) {
		//입력받은 패스워드를 암호화 하여 DB에 저장된 패스워드와 대조
		return passwordEncoder.matches(password, user.getPassword());
	}

	@Override
	public void updatePassword(String password, User user) {
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}

	@Override
	public boolean nicknameDupCheck(String nickName) {
		boolean result = userRepository.findByNickname(nickName).isPresent();

		return result;
	}

	public String generateRandomNickname() {
		int randomIndex = 0;
		String randomNickname = null;

		random.setSeed(System.currentTimeMillis() * 10_000);
		randomIndex = random.nextInt(frontWords.getSize());
		randomNickname = frontWords.getWord(randomIndex);

		random.setSeed(System.currentTimeMillis() * 20_000);
		randomIndex = random.nextInt(backWords.getSize());
		randomNickname += backWords.getWord(randomIndex);

		return randomNickname;
	}

	/**
	 *
	 * @param userIdx
	 * @return false : emailAuth 테이블에 존재하는 유저 /  true : emailAuth 테이블에 존재하지 않는 유저
	 */
	@Override
	public boolean checkEmailAuthToken(Long userIdx) {
		User user = userRepository.getOne(userIdx);
		Optional<EmailAuth> emailAuth = emailAuthRepository.findByUserIdx(user);

		if(emailAuth.isPresent()) {
			return false;
		}

		return true;
	}

	@Override
	public void saveEmailAuthToken(Long userIdx, String token) {
		// 1. 조회 -> 이미 발급받은 token이 있는지 db에서 조회
		if(checkEmailAuthToken(userIdx)) {
			// 2-1. db에 결과값이 없으면 새로 입력
			EmailAuth emailAuth = new EmailAuth();
			emailAuth.setUserIdx(userRepository.getOne(userIdx));
			emailAuth.setToken(token);

			emailAuthRepository.save(emailAuth);
		} else {
			// 2-2. db에 결과값이 있으면 token 값 변경
			User user = userRepository.getOne(userIdx);
			EmailAuth emailAuth = emailAuthRepository.findByUserIdx(user).get();
			emailAuth.setToken(token);

			emailAuthRepository.save(emailAuth);
		}
	}

	@Override
	public void sendEmail(Long userIdx, String token) throws MessagingException {
		saveEmailAuthToken(userIdx, token);

		// 이메일 발송
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

		User user = userRepository.getOne(userIdx);
		String email = user.getEmailId()+"@"+user.getEmailDomain();

		helper.setTo(email);
		helper.setFrom("Mela!");
		helper.setSubject("[Mela!] 이메일 계정을 인증해주세요");

		String htmlContent = "<html><body>";
		htmlContent += "<p>"+user.getEmailId()+"님 안녕하세요.</p>";
		htmlContent += "<p>Mela!를 정상적으로 이용하기 위해서는 이메일 인증을 해주세요</p>";
		htmlContent += "<p>아래 링크를 누르시면 인증이 완료됩니다.</p>";
		htmlContent += "<a href=\"http://localhost:8080/api/v1/auth/verify?token=" + token + "\">인증 링크</a>";
		htmlContent += "</body></html>";

		helper.setText(htmlContent, true);

		mailSender.send(mimeMessage);
	}

	/**
	 * 
	 * @param userIdx
	 * @param token
	 * @return true: 인증됨 / false: 인증 안됨
	 */
	@Override
	public boolean verifyEmail(Long userIdx, String token) {
		User user = userRepository.getOne(userIdx);
		Optional<EmailAuth> emailAuth = emailAuthRepository.findByUserIdx(user);

		if(emailAuth.isPresent()) {
			if(emailAuth.get().getToken().equals(token)) {
				try{
					// 토큰 유효성 확인
					JwtTokenUtil.handleError(token);
					System.out.println("token 유효성");

					// 인증 회원으로 전환
					user.setUserType("auth");
					userRepository.save(user);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

}
