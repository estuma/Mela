package com.ssafy.api.user.request;

import com.ssafy.db.entity.File;
import com.ssafy.db.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 포트폴리오 뮤직 API ([POST] /) 요청에 필요한 리퀘스트 바디 정의.
 */
@Getter
@Setter
@ApiModel("PortfolioMusicPostRequest")
public class PortfolioMusicPostReq {
    @ApiModelProperty(name = "포트폴리오에 저장할 곡명", example = "Mela!")
    String title;

    @ApiModelProperty(name = "포트폴리오에 저장할 곡의 고정(pinned) 여부", example = "Mela!")
    boolean pinFixed;

    @ApiModelProperty(name = "포트폴리오에 저장할 곡의 업로더 Idx", example = "1")
    User userIdx;

//    AUTO_INCREMENT
//    @ApiModelProperty(name = "포트폴리오에 저장할 곡 Table Record (1 Row)의 idx", example = "1")
//    File musicFileIdx;

//    AUTO_INCREMENT
//    @ApiModelProperty(name = "포트폴리오에 저장할 가사 Table Record (1 Row)의 idx", example = "1")
//    File lyricFileIdx;

//    AUTO_INCREMENT
//    @ApiModelProperty(name = "포트폴리오에 저장할 앨범아트 Table Record (1 Row)의 idx", example = "1")
//    File albumArtFileIdx;
}