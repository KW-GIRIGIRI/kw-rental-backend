package com.girigiri.kwrental.auth.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import com.girigiri.kwrental.auth.dto.request.KwangwoonMemberRetrieveRequest;
import com.girigiri.kwrental.auth.dto.response.KwangwoonMemberResponse;

public interface KwangwoonMemberService {

	@PostExchange("/ext/out/SelectFindAllList.do")
	List<KwangwoonMemberResponse> retrieve(
		@RequestBody final KwangwoonMemberRetrieveRequest kwangWoonMemberRetrieveRequest);
}
