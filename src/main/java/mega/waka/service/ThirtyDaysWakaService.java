package mega.waka.service;

import mega.waka.entity.Member;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.editor.ThirtyDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.language.ThirtyDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.entity.project.ThirtyDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.editor.ThirtyDaysEditorRepository;
import mega.waka.repository.language.ThirtyDaysLanguageRepository;
import mega.waka.repository.project.ThirtyDaysProjectRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThirtyDaysWakaService {

    private final ThirtyDaysProjectRepository thirtyDaysProjectRepository;
    private final ThirtyDaysEditorRepository thirtyDaysEditorRepository;
    private final ThirtyDaysLanguageRepository thirtyDaysLanguageRepository;
    private final MemberRepository memberRepository;
    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();

    public ThirtyDaysWakaService(ThirtyDaysProjectRepository thirtyDaysProjectRepository, ThirtyDaysEditorRepository thirtyDaysEditorRepository, ThirtyDaysLanguageRepository thirtyDaysLanguageRepository, MemberRepository memberRepository) {
        this.thirtyDaysProjectRepository = thirtyDaysProjectRepository;
        this.thirtyDaysEditorRepository = thirtyDaysEditorRepository;
        this.thirtyDaysLanguageRepository = thirtyDaysLanguageRepository;
        this.memberRepository = memberRepository;
    }

    public void update_ThirtyDays(){
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://wakatime.com/api/v1/users/current/stats/last_30_days";
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
                //System.out.println("data = " + data);
                JSONArray categories = (JSONArray) data.get("categories");
                System.out.println("categories = " + categories);
                JSONObject index = (JSONObject) categories.get(0);
                member.setThirtyDays(index.get("text").toString());
                memberRepository.save(member);

                JSONArray languages = (JSONArray) data.get("languages");
                JSONArray editors = (JSONArray) data.get("editors");
                JSONArray projects = (JSONArray) data.get("projects");

                if(languages.isEmpty() || editors.isEmpty() || projects.isEmpty()) continue;
                else {
                    set_Language(languages);
                    set_Project(projects);
                    set_Editor(editors);
                }
                set_Member_By_Language(member);
                set_Member_By_Editor(member);
                set_Member_By_Project(member);
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
        if (member.getThirtyDaysLanguages().size() == 0) {
            for (String key : languageList.keySet()) {
                ThirtyDaysLanguage language = new ThirtyDaysLanguage().builder()
                        .name(key)
                        .time(languageList.get(key))
                        .build();
                thirtyDaysLanguageRepository.save(language);
                member.getThirtyDaysLanguages().add(language);
                memberRepository.save(member);
            }
        } else {
            for (int i = 0; i < member.getThirtyDaysLanguages().size(); i++) {
                boolean flag = false;
                String name = "";
                for (String key : languageList.keySet()) {
                    name = key;
                    if (member.getThirtyDaysLanguages().get(i).getName().equals(key)) {
                        member.getThirtyDaysLanguages().get(i).setTime(languageList.get(key));
                        flag = true;
                    }
                    if(member.getThirtyDaysLanguages().get(i).getTime().equals("0:0")){
                        member.getThirtyDaysLanguages().remove(i);
                    }
                }
                if (flag == false) {
                    ThirtyDaysLanguage language = new ThirtyDaysLanguage().builder()
                            .name(name)
                            .time(languageList.get(name))
                            .build();
                    thirtyDaysLanguageRepository.save(language);
                    member.getThirtyDaysLanguages().add(language);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Project(Member member) {
        if(member.getThirtyDaysProjects().size()==0){
            for (String key : projectList.keySet()) {
                ThirtyDaysProject project = new ThirtyDaysProject().builder()
                        .name(key)
                        .time(projectList.get(key))
                        .build();
                thirtyDaysProjectRepository.save(project);
                member.getThirtyDaysProjects().add(project);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getThirtyDaysProjects().size();i++){
                boolean flag = false;
                String name="";
                for(String key : projectList.keySet()){
                    name = key;
                    if(member.getThirtyDaysProjects().get(i).getName().equals(key)){
                        member.getThirtyDaysProjects().get(i).setTime(projectList.get(key));
                        flag=true;
                    }
                    if(member.getThirtyDaysProjects().get(i).getTime().equals("0:0")){
                        member.getThirtyDaysProjects().remove(i);
                    }
                }
                if(flag==false){
                    ThirtyDaysProject project = new ThirtyDaysProject().builder()
                            .name(name)
                            .time(projectList.get(name))
                            .build();
                    thirtyDaysProjectRepository.save(project);
                    member.getThirtyDaysProjects().add(project);
                    memberRepository.save(member);
                }
            }
        }
    }
    private void set_Member_By_Editor(Member member) {
        if(member.getThirtyDaysEditors().size()==0){
            for (String key : editList.keySet()) {

                ThirtyDaysEditor editor = new ThirtyDaysEditor().builder()
                        .name(key)
                        .time(editList.get(key))
                        .build();
                thirtyDaysEditorRepository.save(editor);
                member.getThirtyDaysEditors().add(editor);
                memberRepository.save(member);
            }
        }
        else{
            for(int i=0; i<member.getThirtyDaysEditors().size();i++){
                boolean flag = false;
                String name="";
                for(String key : editList.keySet()){
                    name = key;
                    if(member.getThirtyDaysEditors().get(i).getName().equals(key)){
                        member.getThirtyDaysEditors().get(i).setTime(editList.get(key));
                        flag=true;
                    }
                    if(member.getThirtyDaysEditors().get(i).getTime().equals("0:0")){
                        member.getThirtyDaysEditors().remove(i);
                    }
                }
                if(flag==false){
                    ThirtyDaysEditor editor = new ThirtyDaysEditor().builder()
                            .name(name)
                            .time(editList.get(name))
                            .build();
                    thirtyDaysEditorRepository.save(editor);
                    member.getThirtyDaysEditors().add(editor);
                    memberRepository.save(member);
                }
            }
        }
    }
}
