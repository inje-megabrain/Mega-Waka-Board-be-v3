package mega.waka.controller;

import io.swagger.v3.oas.annotations.Operation;
import mega.waka.service.MemberService;
import mega.waka.service.WakaTimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/waka")
public class WakaController {

    private final WakaTimeService wakaTimeService;
    private final MemberService memberService;

    public WakaController(WakaTimeService wakaTimeService, MemberService memberService) {
        this.wakaTimeService = wakaTimeService;
        this.memberService = memberService;
    }

    @PostMapping("/{name}")
    @Operation(summary = "Create Member API", description = "새로운 멤버를 추가합니다.")
    public ResponseEntity createMember(@PathVariable String name, @RequestParam String organization, @RequestParam String apiKey) {
        try{
            memberService.add_Member_By_apiKey(name, organization, apiKey);
            return new ResponseEntity("success", HttpStatus.OK);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/members")
    @Operation(summary = "Get Member List API", description = "멤버 리스트를 조회합니다.")
    public ResponseEntity getMemberList() {
        try{
            return new ResponseEntity(wakaTimeService.get_Member_List(), HttpStatus.OK);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/members")
    @Operation(summary = "수동 api 갱신 API", description = "코딩 시간을 수동 갱신합니다.")
    public ResponseEntity getMemberTimeByApiKey(@RequestParam int date){
        try{

            return new ResponseEntity(wakaTimeService.get_Member_Time_By_ApiKey(date), HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity(e.fillInStackTrace(), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity(e.fillInStackTrace(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/members/{id}")
    @Operation(summary = "delete member API", description = "멤버를 삭제합니다.")
    public ResponseEntity deleteMember(@PathVariable String id){
        try{
            memberService.delete_member(UUID.fromString(id));
            return new ResponseEntity("해당 멤버가 삭제되었습니다.", HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity(e.fillInStackTrace(), HttpStatus.BAD_REQUEST);
        }
    }
}
