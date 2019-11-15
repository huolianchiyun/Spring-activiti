package boot.spring.configs;


import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateConfig {
    private final static String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String DateFormat = "yyyy-MM-dd";
    private static final String TimeFormat = "yyyy-MM-dd";

    /**
     * 全局设置 jackson 序列化实体类时的日期格式
     * 也可不采用全局设置，可以在对应实体类的 Date 属性上加 @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss") 注解局部设置
     * 注意：局部设置会覆盖全局设置
     */
    @Primary
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateTimeFormat)));
        };
    }
}

