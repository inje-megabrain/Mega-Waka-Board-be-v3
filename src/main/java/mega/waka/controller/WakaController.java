package mega.waka.controller;

import io.swagger.v3.oas.annotations.Operation;
import mega.waka.entity.dto.*;
import mega.waka.service.*;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/waka")
public class WakaController {

    private final FourteenDaysWakaService wakaTimeService;
    private final OneDaysWakaService oneDaysWakaService;
    private final SevenDaysWakaService sevenDaysWakaService;
    private final ThirtyDaysWakaService thirtyDaysWakaService;
    private final RedisUtil redisUtil;
    private final MemberService memberService;

    public WakaController(FourteenDaysWakaService fourtyDaysWakaService, OneDaysWakaService oneDaysWakaService, SevenDaysWakaService sevenDaysWakaService, ThirtyDaysWakaService thirtyDaysWakaService, RedisUtil redisUtil, MemberService memberService) {
        this.wakaTimeService = fourtyDaysWakaService;
        this.oneDaysWakaService = oneDaysWakaService;
        this.sevenDaysWakaService = sevenDaysWakaService;
        this.thirtyDaysWakaService = thirtyDaysWakaService;
        this.redisUtil = redisUtil;
        this.memberService = memberService;
    }

    @PostMapping("/{github_Id}")
    @Operation(summary = "Create Member API", description = "name = 이름 정자 표기, organization = 메가브레인, 돗가비 정자 표기, apiKey = wakatime api key, githubId = github id, department = 백엔드 or 프론트엔드.. etc")
    public ResponseEntity createMember(@RequestParam String name, @RequestParam String organization, @RequestParam String apiKey, @PathVariable String github_Id, @RequestParam String department) {
        try{
            if(Organization.Megabrain.getName().equals(organization) || Organization.Dotgabi.getName().equals(organization)){
                if(memberService.Authentication_apiKey(apiKey).equals("200")){
                    memberService.add_Member_By_apiKey(name, organization, apiKey, github_Id,department);
                    return new ResponseEntity("success", HttpStatus.OK);
                }
                else return new ResponseEntity("apiKey가 올바르지 않습니다.",HttpStatus.BAD_REQUEST);

            }

            else return new ResponseEntity("organization은 메가브레인, 돗가비 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/members")
    @Operation(summary = "Get Member List API", description = "멤버 리스트를 조회합니다.")
    public ResponseEntity getMemberList() {
        try{
            return new ResponseEntity(memberService.memberList(), HttpStatus.OK);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/members/{id}")
    @Operation(summary = "멤버 상세 조회 API", description = "멤버를 상세 조회합니다. date =7 or 30")
    public ResponseEntity getMember_Info(@PathVariable String id, @RequestParam int date){
        try{
            if(date==7){
                ResponseInfoDto dto =memberService.get_Member_info_day(UUID.fromString(id));
                Map<String, ResponseSummariesDto> map = memberService.get_totals_Member(UUID.fromString(id));
                JSONArray arr = new JSONArray();
                arr.add(dto);
                arr.add(map);
                return new ResponseEntity(arr,HttpStatus.OK);
            }
            else if(date ==30) {
                ResponseInfoThirtyDaysDto dto = memberService.get_Member_info_ThirtyDays(UUID.fromString(id));
                ResponseThirtyDaysSummariesDto dtos = oneDaysWakaService.get_One_Month(UUID.fromString(id));
                JSONArray arr = new JSONArray();
                arr.add(dto);
                arr.add(dtos);
                System.out.println(dtos.getSummariesEditors());
                return new ResponseEntity(arr,HttpStatus.OK);
            }
            else return new ResponseEntity("7 or 30 중 하나를 입력해야 됩니다.",HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/members")
    @Operation(summary = "수동 api 갱신 API", description = "코딩 시간을 수동 갱신합니다. date = 1,7,14,30")
    public ResponseEntity getMemberTimeByApiKey(@RequestParam int date){
        try{
            if(date ==1) oneDaysWakaService.update_OneDays();
            else if(date ==30) {
                thirtyDaysWakaService.update_ThirtyDays();
                //redisUtil.save_Redis_ThirtyDays();
            }
            else if(date ==7) {
                sevenDaysWakaService.update_SevenDays();
                //redisUtil.save_Redis_SevenDays();
            }
            else if(date ==14) wakaTimeService.update_FourtyDays();
            else return new ResponseEntity("date는 1, 7, 14, 30 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);
            return new ResponseEntity("susccess", HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/members/{id}")
    @Operation(summary = "apikey update API", description = "apikey를 갱신합니다.")
    public ResponseEntity updateApiKey(@PathVariable String id, @RequestParam String apiKey){
        try{
            UUID uuid = UUID.fromString(id);
            memberService.update_apiKey(uuid, apiKey);
            return new ResponseEntity("success", HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/members/{id}")
    @Operation(summary = "delete member API", description = "멤버를 삭제합니다.")
    public ResponseEntity deleteMember(@PathVariable String id){
        try{
            memberService.delete_member(UUID.fromString(id));
            return new ResponseEntity("해당 멤버가 삭제되었습니다.", HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
