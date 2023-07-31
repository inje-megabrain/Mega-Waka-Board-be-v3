package mega.waka.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mega.waka.entity.Money;
import java.time.LocalDateTime;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMemberDto {
    private UUID id;
    private String name;
    private String organization;
    private String sevenDays;
    private String image;
    private LocalDateTime startDate;
    private String department;
    private Money money;
}
