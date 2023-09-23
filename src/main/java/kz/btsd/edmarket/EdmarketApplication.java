package kz.btsd.edmarket;

import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.model.ModuleProgressDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories
@EnableAsync
@EnableElasticsearchRepositories(basePackages = "elastic")
@EnableScheduling
@SpringBootApplication
public class EdmarketApplication {

    public static void main(String[] args) {

        SpringApplication.run(EdmarketApplication.class, args);
    }

    public PropertyMap<ModuleDto, ModuleProgressDto> skipModifiedFieldsMap = new PropertyMap<ModuleDto, ModuleProgressDto>() {
        protected void configure() {
            skip().setSections(null);
        }
    };
    public PropertyMap<Module, Module> skipModuleSections = new PropertyMap<Module, Module>() {
        protected void configure() {
            skip().setSections(null);
        }
    };
    public PropertyMap<SectionFullDto, Section> skipSectionSubsections = new PropertyMap<SectionFullDto, Section>() {
        protected void configure() {
            skip().setSubsections(null);
        }
    };
    public PropertyMap<SectionFullDto, SectionProgressDto> skipSection = new PropertyMap<SectionFullDto, SectionProgressDto>() {
        protected void configure() {
            skip().setSubsections(null);
        }
    };
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(skipModifiedFieldsMap);
        modelMapper.addMappings(skipSection);
        modelMapper.addMappings(skipModuleSections);
        modelMapper.addMappings(skipSectionSubsections);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}
