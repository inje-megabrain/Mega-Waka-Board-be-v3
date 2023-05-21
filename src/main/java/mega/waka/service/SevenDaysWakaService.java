package mega.waka.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mega.waka.entity.Member;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.editor.SevenDaysEditorRepository;
import mega.waka.repository.language.SevenDaysLanguageRepository;
import mega.waka.repository.project.SevendaysProjectRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SevenDaysWakaService {

    private final SevenDaysEditorRepository sevenDaysEditorRepository;

    private final SevenDaysLanguageRepository sevenDaysLanguageRepository;

    private final SevendaysProjectRepository sevendaysProjectRepository;
    private final MemberRepository memberRepository;

    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();

    public SevenDaysWakaService(SevenDaysEditorRepository sevenDaysEditorRepository, SevenDaysLanguageRepository sevenDaysLanguageRepository, SevendaysProjectRepository sevendaysProjectRepository, MemberRepository memberRepository) {
        this.sevenDaysEditorRepository = sevenDaysEditorRepository;
        this.sevenDaysLanguageRepository = sevenDaysLanguageRepository;
        this.sevendaysProjectRepository = sevendaysProjectRepository;
        this.memberRepository = memberRepository;
    }
    @JsonIgnore
    public void update_SevenDays(){
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/stats/last_7_days";
            for (Member member : members) {

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

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
                JSONObject data = (JSONObject) jsonObject.get("data");

                JSONArray categories = (JSONArray) data.get("categories");
                JSONObject index = (JSONObject) categories.get(0);
                member.setSevenDays(index.get("text").toString());
                memberRepository.save(member);
                JSONArray languages = (JSONArray) data.get("languages");
                JSONArray editors = (JSONArray) data.get("editors");
                JSONArray projects = (JSONArray) data.get("projects");

                set_Member_By_Language(member);
                set_Member_By_Editor(member);
                set_Member_By_Project(member);

                DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                if(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN).equals("금요일")){
                    int money = member.getMoney().getAmount();
                    String [] time = member.getSevenDays().split("hrs");
                    int hour = Integer.parseInt(time[0]);
                    money += hour*9620;
                    member.getMoney().setAmount(money);
                    member.getMoney().setUpdateDate(LocalDate.now());
                    memberRepository.save(member);
                }
                editList.clear();
                languageList.clear();
                projectList.clear();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
    private void set_Member_By_Language(Member member) {
        if (member.getSevenlanguages().size() == 0) {
            for (String key : languageList.keySet()) {
                SevenDaysLanguage language = new SevenDaysLanguage().builder()
                        .name(key)
                        .time(languageList.get(key))
                        .build();
                sevenDaysLanguageRepository.save(language);
                member.getSevenlanguages().add(language);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getSevenlanguages().size(); i++) {
                boolean flag = false;
                String name = "";
                for (String key : languageList.keySet()) {
                    name = key;
                    if (member.getSevenlanguages().get(i).getName().equals(key)) {
                        member.getSevenlanguages().get(i).setTime(languageList.get(key));
                        flag = true;
                    }
                    if(member.getSevenlanguages().get(i).getTime().equals("0:0")){
                        member.getSevenlanguages().remove(i);
                    }
                }
                if (flag == false) {
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
    private void set_Member_By_Project(Member member) {
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
                    if(member.getSevenprojects().get(i).getTime().equals("0:0")){
                        member.getSevenprojects().remove(i);
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
    private void set_Member_By_Editor(Member member) {
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
                        flag=true;
                    }
                    if(member.getSeveneditors().get(i).getTime().equals("0:0")){
                        member.getSeveneditors().remove(i);
                    }
                }
                if(flag==false){
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
