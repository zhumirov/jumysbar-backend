package kz.btsd.edmarket.review.model;

import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserShortDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Review convertToEntity(ReviewRequest reviewRequest) {
       // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //todo убрать стоит в общей конфигурации
        return modelMapper.map(reviewRequest, Review.class);
    }

    public ReviewDto convertToDto(Review review, User user) {
        ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
        reviewDto.setAuthor(user.getName());
        return reviewDto;
    }
}
