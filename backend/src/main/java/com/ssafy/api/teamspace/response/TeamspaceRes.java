package com.ssafy.api.teamspace.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.db.entity.File;
import com.ssafy.db.entity.Teamspace;
import com.ssafy.db.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@ApiModel("TeamspaceResponse")
public class TeamspaceRes {
    @ApiModelProperty(name="teamspace id")
    Long teamspaceIdx;
    @ApiModelProperty(name="teamspace teamName")
    String teamName;
    @ApiModelProperty(name="teamspace startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate startDate;
    @ApiModelProperty(name="teamspace endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate endDate;
    @ApiModelProperty(name="teamspace description")
    String teamDescription;
    @ApiModelProperty(name="teamspace host")
    User host;
    @ApiModelProperty(name="teamspace 썸네일")
    File teamspace_picture_file_idx;
    @ApiModelProperty(name="teamspace 배경사진")
    File teamspace_background_picture_file_idx;

    public static TeamspaceRes of(Teamspace teamspace) {
        TeamspaceRes res = new TeamspaceRes();
        res.setTeamspaceIdx(teamspace.getTeamspaceIdx());
        res.setStartDate(teamspace.getStartDate());
        res.setEndDate(teamspace.getEndDate());
        res.setTeamDescription(teamspace.getTeamDescription());
        res.setHost(teamspace.getHost());
        res.setTeamspace_picture_file_idx(teamspace.getTeamspace_picture_file_idx());
        res.setTeamspace_background_picture_file_idx(teamspace.getTeamspace_background_picture_file_idx());
        return res;
    }

}
