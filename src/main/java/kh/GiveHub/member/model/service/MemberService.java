package kh.GiveHub.member.model.service;

import kh.GiveHub.member.model.vo.Member;
import kh.GiveHub.payment.model.vo.Payment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kh.GiveHub.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberMapper mapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Member m = new Member();
		m.setMemId(username);

		Member loginMember = mapper.login(m);

		if (loginMember == null) {
			// 사용자가 없으면 UsernameNotFoundException 발생
			throw new UsernameNotFoundException(username + "을 찾을 수 없습니다.");
		}

		List<GrantedAuthority> authorities = new ArrayList<>();

		// memType을 정수로 변환하여 비교
		String memTypeStr = loginMember.getMemType();
		int memType = 0;

		if (memTypeStr != null && !memTypeStr.trim().isEmpty()) {
			try {
				// trim()으로 공백 제거, parseInt로 정수 변환
				memType = Integer.parseInt(memTypeStr.trim());
			} catch (NumberFormatException e) {
				// 변환 실패 시 기본값 0 유지
			}
		}

		switch (memType) { // 이제 정수 2와 비교
			case 2: // 관리자
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				break;
			case 1: // 주최자
				authorities.add(new SimpleGrantedAuthority("ROLE_ORGANIZER"));
				break;
			case 0: // 일반 회원
			default:
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				break;
		}

		return new org.springframework.security.core.userdetails.User(
				loginMember.getMemId(),
				loginMember.getMemPwd(),
				authorities
		);
	}

	public ArrayList<Member> selectMemberList() {
		return mapper.selectMemberList();
	}

	public Member login(Member m) {
		return mapper.login(m);
	}

	public Member selectNo(int no) {
		return mapper.selectNo(no);
	}

	public int adminMemberUpdate(Member m) {
		return mapper.adminMemberUpdate(m);
	}

	public int adminMemberDelete(Member m) {
		return mapper.adminMemberDelete(m);
	}

	public int checkId(String id) {
		return mapper.checkId(id);
	}

	public int memberJoin(Member m) {
		return mapper.memberJoin(m);
	}

	public int checkIdDuplication(String email) {
		return mapper.checkIdDuplication(email);

	}

	public int editMemberInfo(Member m) {
		return mapper.editMemberInfo(m);
	}

	public String findIdByEmail(String email) {
		return mapper.findMyId(email);
	}

	public int updateTempPwd(String email, String encodePwd) {
		return mapper.updateTempPwd(email,encodePwd);
	}

	public String findMemNameByEmail(String email) {
		return mapper.findMemNameByEmail(email);
	}

	public ArrayList<Payment> selectDonationList(int no, int type) {
		return mapper.selectDonationList(no, type);
	}

	public int deleteMember(String login) { return mapper.deleteMember(login);}

	public int checkEmail(String email) {
		return mapper.checkEmail(email);
	}

	public int updateRank(HashMap<String, Object> rankMap) {
		return mapper.updateRank(rankMap);
	}

}
