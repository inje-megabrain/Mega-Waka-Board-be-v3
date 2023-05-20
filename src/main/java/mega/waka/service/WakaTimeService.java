package mega.waka.service;

import mega.waka.entity.*;
import mega.waka.repository.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WakaTimeService {

    private final MemberRepository memberRepository;
    private final EditorRepository editorRepository;
    private final LanguageRepository languageRepository;
    private final ProjectRepository projectRepository;
    private final SevenDaysLanguageRepository sevenDaysLanguageRepository;
    private final SevendaysProjectRepository sevendaysProjectRepository;
    private final SevenDaysEditorRepository sevenDaysEditorRepository;
    private Map<String,String> languageList  = new HashMap<>();
    private Map<String,String> projectList  = new HashMap<>();
    private Map<String,String> editList  = new HashMap<>();

    public WakaTimeService(MemberRepository memberRepository, EditorRepository editorRepository, LanguageRepository languageRepository, ProjectRepository projectRepository, SevenDaysLanguageRepository sevenDaysLanguageRepository, SevendaysProjectRepository sevendaysProjectRepository, SevenDaysEditorRepository sevenDaysEditorRepository) {
        this.memberRepository = memberRepository;
        this.editorRepository = editorRepository;
        this.languageRepository = languageRepository;
        this.projectRepository = projectRepository;
        this.sevenDaysLanguageRepository = sevenDaysLanguageRepository;
        this.sevendaysProjectRepository = sevendaysProjectRepository;
        this.sevenDaysEditorRepository = sevenDaysEditorRepository;
    }
    public void update_apiKey(UUID id, String apiKey) {  // apiKey 변경 api
       Optional<Member> member = memberRepository.findById(id);
       member.orElseThrow(()->{
              throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
       });
        member.get().setSecretKey(apiKey);
        memberRepository.save(member.get());
    }
    public void add_Member_By_apiKey(String name, String organization, String apiKey) {  // member 생성 api
       Optional<Member> findMember = memberRepository.findByNameAAndOrganization(name,organization);
       findMember.orElseThrow(()->{
              throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
       });
       Member member = new Member();
       member.setSecretKey(apiKey);
       member.setName(name);
       member.setId(UUID.randomUUID());
       member.setFourteenDays("0:0");
       member.setSevenDays("0:0");
       member.setThirtyDays("0:0");
       member.setOrganization(organization);
       member.setUpdateDate(LocalDate.now());
       member.setStartDate(LocalDate.now());
       memberRepository.save(member);
    }
   /* public void thirtyDays_add(int days){ //30일 데이터 가공
        List<Member> members = memberRepository.findAll();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries";
            for(Member member : members) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("range", "last_" + 1 + "_days");

                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(member.getSecretKey(), "");

                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
                JSONArray data = (JSONArray) jsonObject.get("data");


                JSONObject total = (JSONObject) jsonObject.get("cumulative_total");
                String [] time = total.get("text").toString().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);
                String [] memerTime = member.getThirtyDays().split(":");
                int memberHour = Integer.parseInt(memerTime[0]);
                int memberMinute = Integer.parseInt(memerTime[1]);
                memberHour += hour;
                memberMinute += minute;
                if(memberMinute>=60){
                    memberHour += memberMinute/60;
                    memberMinute = memberMinute%60;
                }
                if(member.getUpdateDate().isEqual(LocalDate.now().minusDays(30))){
                    member.setUpdateDate(LocalDate.now());
                }


            }
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }*/
    public List<Member> get_Member_List() {  // member 리스트 조회 api
        return memberRepository.findAll();
    }
    public String get_Member_Time_By_ApiKey(int days) {  // member의 코딩 시간 조회 api
        List<Member> members = memberRepository.findAll();
        String responseData="";
        String returnvalue="";
        Map<String,String> editorList = new HashMap<>();
        Map<String,String> languageList = new HashMap<>();
        Map<String,String> projectList = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries";
            for(Member member : members){
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("range","last_"+days+"_days");

                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(member.getSecretKey(),"");

                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                        );
                responseData = response.getBody();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(responseData);
                JSONArray data = (JSONArray) jsonObject.get("data");
                

                
                JSONObject total = (JSONObject) jsonObject.get("cumulative_total");
                if(days == 7) member.setSevenDays(total.get("text").toString());
                else if(days==14) member.setFourteenDays(total.get("text").toString());
                for(int i=0;i<data.size();i++){
                    JSONObject obj = (JSONObject) data.get(i);
                    JSONArray languages = (JSONArray) obj.get("languages");
                    JSONArray editors = (JSONArray) obj.get("editors");
                    JSONArray projects = (JSONArray) obj.get("projects");

                    if(languages.isEmpty() || editors.isEmpty() || projects.isEmpty()) continue;
                    else {
                        set_Language(languages);
                        set_Project(projects);
                        set_Editor(editors);
                    }
                }
                set_Member_By_Language(member,days);
                set_Member_By_Editor(member,days);
                set_Member_By_Project(member,days);
                editorList.clear();
                languageList.clear();
                projectList.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }
    private Map<String,String> set_Language(JSONArray languages){

        for(int j=0;j<languages.size();j++) {
            JSONObject index = (JSONObject) languages.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(languageList.containsKey(name)){
                String[] time = languageList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                languageList.replace(name,hour+":"+min);
            }
            else{

                languageList.put(name,hour+":"+min);
            }
        }

        return languageList;

    }
    private Map<String,String> set_Project(JSONArray projects){

        for(int j=0;j<projects.size();j++) {
            JSONObject index = (JSONObject) projects.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(projectList.containsKey(name)){
                String[] time = projectList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                projectList.replace(name,hour+":"+min);
            }
            else{

                projectList.put(name,hour+":"+min);
            }
        }
        return projectList;

    }
    private Map<String,String> set_Editor(JSONArray editor){

        for(int j=0;j<editor.size();j++) {
            JSONObject index = (JSONObject) editor.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(editList.containsKey(name)){
                String[] time = editList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                editList.replace(name,hour+":"+min);
            }
            else{

                editList.put(name,hour+":"+min);
            }
        }
        return editList;

    }
    //------------------------------------set----------------------------------------/
    private void set_Member_By_Language(Member member,int days) {
        if (days == 14) {
            if(member.getLanguages().size()==0){
                for (String key : languageList.keySet()) {

                    Language language = new Language().builder()
                            .name(key)
                            .time(languageList.get(key))
                            .build();
                    languageRepository.save(language);
                    member.getLanguages().add(language);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getLanguages().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : languageList.keySet()){
                        name =key;
                        if(member.getLanguages().get(i).getName().equals(key)){
                            member.getLanguages().get(i).setTime(languageList.get(key));
                            flag = true;
                        }
                    }
                    if(flag==false){
                        Language language = new Language().builder()
                                .name(name)
                                .time(languageList.get(name))
                                .build();
                        languageRepository.save(language);
                        member.getLanguages().add(language);
                        memberRepository.save(member);
                    }
                }
            }
        } else {
            if(member.getSevenlanguages().size()==0){
                for (String key : languageList.keySet()) {

                    SevenDaysLanguage language = new SevenDaysLanguage().builder()
                            .name(key)
                            .time(languageList.get(key))
                            .build();
                    sevenDaysLanguageRepository.save(language);
                    member.getSevenlanguages().add(language);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getSevenlanguages().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : languageList.keySet()){
                        name=key;
                        if(member.getSevenlanguages().get(i).getName().equals(key)){
                            member.getSevenlanguages().get(i).setTime(languageList.get(key));
                            flag=true;
                        }
                    }
                    if(!flag){
                        SevenDaysLanguage language = new SevenDaysLanguage().builder()
                                .name(name)
                                .time(languageList.get(name))
                                .build();
                        sevenDaysLanguageRepository.save(language);
                        member.getSevenlanguages().add(language);
                        memberRepository.save(member);
                    }
                }
            }
        }
    }
    private void set_Member_By_Project(Member member,int days) {
        if (days == 14) {
            if(member.getProjects().size()==0){
                for (String key : projectList.keySet()) {

                    Project project = new Project().builder()
                            .name(key)
                            .time(projectList.get(key))
                            .build();
                    projectRepository.save(project);
                    member.getProjects().add(project);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getProjects().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : projectList.keySet()){
                        name = key;
                        if(member.getProjects().get(i).getName().equals(key)){
                            member.getProjects().get(i).setTime(projectList.get(key));
                            flag=true;
                        }
                    }
                    if(flag==false){
                        Project project = new Project().builder()
                                .name(name)
                                .time(projectList.get(name))
                                .build();
                        projectRepository.save(project);
                        member.getProjects().add(project);
                        memberRepository.save(member);
                    }
                }
            }
        } else {
            if(member.getSevenprojects().size()==0){
                for (String key : projectList.keySet()) {

                    SevenDaysProject project = new SevenDaysProject().builder()
                            .name(key)
                            .time(projectList.get(key))
                            .build();
                    sevendaysProjectRepository.save(project);
                    member.getSevenprojects().add(project);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getSevenprojects().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : projectList.keySet()){
                        name = key;
                        if(member.getSevenprojects().get(i).getName().equals(key)){
                            member.getSevenprojects().get(i).setTime(projectList.get(key));
                            flag=true;
                        }

                    }
                    if(flag==false){
                        SevenDaysProject project = new SevenDaysProject().builder()
                                .name(name)
                                .time(projectList.get(name))
                                .build();
                        sevendaysProjectRepository.save(project);
                        member.getSevenprojects().add(project);
                        memberRepository.save(member);
                    }
                }
            }
        }
    }

    private void set_Member_By_Editor(Member member,int days) {
        if (days == 14) {
            if(member.getEditors().size()==0){
                for (String key : editList.keySet()) {

                    Editor editor = new Editor().builder()
                            .name(key)
                            .time(editList.get(key))
                            .build();
                    editorRepository.save(editor);
                    member.getEditors().add(editor);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getEditors().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : editList.keySet()){
                        name = key;
                        if(member.getEditors().get(i).getName().equals(key)){
                            member.getEditors().get(i).setTime(editList.get(key));
                        }
                    }
                    if(flag==false){
                        Editor editor = new Editor().builder()
                                .name(name)
                                .time(editList.get(name))
                                .build();
                        editorRepository.save(editor);
                        member.getEditors().add(editor);
                        memberRepository.save(member);
                    }
                }
            }
        } else {
            if(member.getSeveneditors().size()==0){
                for (String key : editList.keySet()) {
                    SevenDaysEditor editor = new SevenDaysEditor().builder()
                            .name(key)
                            .time(editList.get(key))
                            .build();
                    sevenDaysEditorRepository.save(editor);
                    member.getSeveneditors().add(editor);
                    memberRepository.save(member);
                }
            }
            else{
                for(int i=0; i<member.getSeveneditors().size();i++){
                    boolean flag = false;
                    String name="";
                    for(String key : editList.keySet()){
                        name = key;
                        if(member.getSeveneditors().get(i).getName().equals(key)){
                            member.getSeveneditors().get(i).setTime(editList.get(key));
                            flag = true;
                        }
                    }
                    if(flag ==false){
                        SevenDaysEditor editor = new SevenDaysEditor().builder()
                                .name(name)
                                .time(editList.get(name))
                                .build();
                        sevenDaysEditorRepository.save(editor);
                        member.getSeveneditors().add(editor);
                        memberRepository.save(member);
                    }
                }
            }
        }
    }
}
