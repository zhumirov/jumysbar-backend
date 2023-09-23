package kz.btsd.edmarket.mentor.model;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MentorConverter {
    @Autowired
    private ModelMapper modelMapper;

    public MentorInviteDto convertToDto(Mentor mentor) {
        return modelMapper.map(mentor, MentorInviteDto.class);
    }

    public Mentor convertToEntity(MentorInviteDto mentorInviteDto) {
        return modelMapper.map(mentorInviteDto, Mentor.class);
    }

}
