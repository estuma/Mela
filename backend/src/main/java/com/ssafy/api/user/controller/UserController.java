package com.ssafy.api.user.controller;

import com.ssafy.api.user.request.UserRegisterPostReq;
import com.ssafy.api.user.request.UserUpdatePostReq;
import com.ssafy.api.user.response.UserRes;
import com.ssafy.api.user.service.UserService;
import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.User;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 유저 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Slf4j
@Api(value = "유저 API", tags = {"User"})
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping()
	@ApiOperation(value = "회원 가입", notes = "<strong>아이디와 패스워드 ...를</strong>를 통해 회원가입 한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> register(
			@RequestBody @ApiParam(value="회원가입 정보", required = true) UserRegisterPostReq registerInfo) {

		//임의로 리턴된 User 인스턴스. 현재 코드는 회원 가입 성공 여부만 판단하기 때문에 굳이 Insert 된 유저 정보를 응답하지 않음.
		User user = userService.createUser(registerInfo);
		user.setUserType("unauth");

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}

	@GetMapping("/myinfo")
	@ApiOperation(value = "회원 본인 정보 조회", notes = "로그인한 회원 본인의 정보를 응답한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<UserRes> getUserInfo(@ApiIgnore Authentication authentication) {
		/**
		 * 요청 헤더 액세스 토큰이 포함된 경우에만 실행되는 인증 처리이후, 리턴되는 인증 정보 객체(authentication) 통해서 요청한 유저 식별.
		 * 액세스 토큰이 없이 요청하는 경우, 403 에러({"error": "Forbidden", "message": "Access Denied"}) 발생.
		 */
		SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();

		String userEmail = userDetails.getUsername();
		User user = userService.getUserByEmail(userEmail);

		return ResponseEntity.status(200).body(UserRes.of(user));
	}

	@PutMapping("/myinfo")
	@ApiOperation(value = "회원 본인 정보 수정", notes = "로그인한 회원 본인의 정보를 수정한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> updateUser(
			@ApiIgnore Authentication authentication,
			@RequestBody @ApiParam(value="회원정보 수정 정보", required = true) UserUpdatePostReq userUpdateInfo){
		SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();


		String userEmail = userDetails.getUsername();
		User user = userService.getUserByEmail(userEmail);

		userService.updateUser(user, userUpdateInfo);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}

	@DeleteMapping("/delete")
	@ApiOperation(value = "회원 탈퇴", notes = "로그인한 회원의 탈퇴를 진행한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> deleteUser(@ApiIgnore Authentication authentication){

		SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();


		String userEmail = userDetails.getUsername();
		User user = userService.getUserByEmail(userEmail);

		userService.deleteUser(user);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}

	@GetMapping("/emailid/{userId}")
	@ApiOperation(value = "이메일 아이디 중복 확인", notes = "기존에 존재하는 아이디인지 확인한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 409, message = "아이디 중복"),
			@ApiResponse(code= 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> checkDupId(@PathVariable String userId) {
		if(!userService.idDupCheck(userId)) {
			return ResponseEntity.status(409).body(BaseResponseBody.of(409, "Conflict"));
		}

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}

	@GetMapping("/nickname/{nickname}")
	@ApiOperation(value = "닉네임 중복 확인", notes = "기존에 존재하는 닉네임인지 확인한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "닉네임 중복 없음"),
			@ApiResponse(code = 409, message = "닉네임 중복 있음"),
			@ApiResponse(code= 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> checkDupNickname(@PathVariable String nickname) {
		if (!userService.nicknameDupCheck(nickname)) {

			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
		}

		return ResponseEntity.status(409).body(BaseResponseBody.of(409, "Conflict"));
	}

	@PostMapping("/password")
	@ApiOperation(value = "비밀번호 확인", notes = "회원 정보 조회 또는 탈퇴를 위해 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호를 대조한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> checkPassword(
			@ApiIgnore Authentication authentication,
			@RequestBody @ApiParam(value="비밀번호", required = true) String inputPassword) {
		SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();
		String userId = userDetails.getUsername();
		User user = userService.getUserByEmail(userId);

		if(userService.checkPassword(inputPassword, user)) {
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
		}

		return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));
	}

	@PutMapping("/password")
	@ApiOperation(value = "비밀번호 변경", notes = "사용자의 비밀번호를 사용자에게 입력 받은 비밀번호로 변경한다")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> updatePassword(
			@ApiIgnore Authentication authentication,
			@RequestBody @ApiParam(value="비밀번호", required = true) String inputPassword) {
		SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();
		String userId = userDetails.getUsername();
		User user = userService.getUserByEmail(userId);

		userService.updatePassword(inputPassword, user);
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}

	@GetMapping("/generaterandomnickname")
	@ApiOperation(value = "랜덤 닉네임 생성", notes = "중복되지 않는 랜덤 닉네임을 생성한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<String> generateRandomNickname() {
		String randomNickname = null;
		long start = System.currentTimeMillis();
		long now;

		//1.5초간 중복없는 랜덤 닉네임 생성
		do {
			now = System.currentTimeMillis();
			randomNickname = userService.generateRandomNickname();

			if (!userService.nicknameDupCheck(randomNickname)) {

				return ResponseEntity.status(200).body(randomNickname);
			}
		} while ((now - start) / 1000 <= 1.5);

		return ResponseEntity.status(500).body("랜덤 닉네임 생성 실패");
	}
}
