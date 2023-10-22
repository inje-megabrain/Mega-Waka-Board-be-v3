package mega.waka.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import mega.waka.entity.Member;
import mega.waka.repository.MemberRepository;
import mega.waka.service.SevenDaysWakaService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DiscordListener extends ListenerAdapter {

    private final MemberRepository memberRepository;
    private final SevenDaysWakaService sevenDaysWakaService;

    public DiscordListener(MemberRepository memberRepository, SevenDaysWakaService sevenDaysWakaService) {
        this.memberRepository = memberRepository;
        this.sevenDaysWakaService = sevenDaysWakaService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getChannel().getId().equals("1090659127417638943")) return;
        super.onSlashCommandInteraction(event);
        User user = event.getUser();
        String returnMessage = "";
        String newMessage = "!!!!!***근무 시간 미달자 ***!!!! \n";
        EmbedBuilder embed = new EmbedBuilder();
        embed.addField("https://megabrain.kr/waka", "", false);
        embed.setThumbnail("https://avatars.githubusercontent.com/inje-megabrain");
        embed.setFooter("메가브레인 와카 봇", "https://avatars.githubusercontent.com/inje-megabrain");
        switch(event.getName()){
            case "전체순위" :
                List<Member> memberList = memberRepository.findAll();
                Map<String, Integer> memberMap = new HashMap<>();
                for(Member member : memberList){
                    Pattern pattern = Pattern.compile("\\b(\\d+) hrs (\\d+) min(s)?\\b");
                    Matcher matcher = pattern.matcher(member.getSevenDays());
                    if(matcher.find()){
                        int hour = Integer.parseInt(matcher.group(1));
                        int minute = Integer.parseInt(matcher.group(2));
                        memberMap.put(member.getName(),hour*60+minute);
                    }
                    else{
                        memberMap.put(member.getName(),0);
                    }
                }
                List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());
                Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 전체 순위");
                embed.setColor(Color.green);
                int cnt=0;
                for(int i=0;i<sortedList.size();i++){
                    int totalMinutes = sortedList.get(i).getValue();
                    int hours = totalMinutes / 60;
                    int minutes = totalMinutes % 60;
                    if (totalMinutes < 10 * 60) {
                        if (totalMinutes > 0) {
                            newMessage += (i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> " + hours + "시간 " + minutes + "분\n";
                            System.out.println(sortedList.get(i).getValue());
                        } else {
                            newMessage += (i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> 0시간 0분\n";
                        }
                    } else {
                        returnMessage += (i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> " + hours + "시간 " + minutes + "분\n";
                    }
                }
                if(cnt==0) newMessage += "현재 근무 시간 미달자가 없습니다.\n";
                embed.setDescription(returnMessage +"\n"+ newMessage);
                break;
            case "개인순위" :
                Member member;
                newMessage ="";
                if(user.getName().equals("_ung_")) member = memberRepository.findByName("신종웅");
                else if(user.getName().equals("jadru"))  member = memberRepository.findByName("박영건");
                else if(user.getName().equals("haroya")) member = memberRepository.findByName("김동현");
                else if(user.getName().equals("LeeByeongJin")) member = memberRepository.findByName("이병진");
                else if(user.getName().equals("ddeltas")) member = memberRepository.findByName("구본웅");
                else if(user.getName().equals("jyh")) member = memberRepository.findByName("정용휘");
                else if(user.getName().equals("gimuhyeon")) member = memberRepository.findByName("김우현");
                else if(user.getName().equals("byeongseok")) member = memberRepository.findByName("성병석");
                else if(user.getName().equals("훈정이")) member = memberRepository.findByName("김훈정");
                else if(user.getName().equals("보경")) member = memberRepository.findByName("문보경");
                else if(user.getName().equals("Zn")) member = memberRepository.findByName("심아연");
                else if(user.getName().equals("Rod12")) member = memberRepository.findByName("이욱진");
                else if(user.getName().equals("재민")) member = memberRepository.findByName("김재민");
                else if(user.getName().equals("hongjihyeon")) member = memberRepository.findByName("홍지현");
                else{
                    member = memberRepository.findByName(user.getName());
                }

                if(member ==null) returnMessage = "해당 멤버가 등록되지 않았습니다.";
                else{
                    List<Member> memberList2 = memberRepository.findAll();
                    Map<String, Integer> memberMap2 = new HashMap<>();
                    for(Member member2 : memberList2){
                        Pattern pattern = Pattern.compile("\\b(\\d+) hrs (\\d+) min(s)?\\b");
                        Matcher matcher = pattern.matcher(member2.getSevenDays());
                        if(matcher.find()){
                            int hour = Integer.parseInt(matcher.group(1));
                            int minute = Integer.parseInt(matcher.group(2));
                            memberMap2.put(member2.getName(),(hour*60)+minute);
                        }
                        else memberMap2.put(member2.getName(),0);
                    }
                    int cnt2=0;
                    List<Map.Entry<String,Integer>> sortedList2 = new ArrayList<>(memberMap2.entrySet());
                    Collections.sort(sortedList2, Map.Entry.comparingByValue(Comparator.reverseOrder()));
                    embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 개인 순위");
                    embed.setColor(Color.green);
                    for(int i=0;i<sortedList2.size();i++){
                        if(sortedList2.get(i).getKey().equals(member.getName())){
                            if(sortedList2.get(i).getValue() <=10*60) {
                                cnt2++;
                                newMessage += i+1+" 등 - "+member.getName()+"\n -> "+sortedList2.get(i).getValue()/60+"시간 "+sortedList2.get(i).getValue()%60+"분\n";
                            }
                            else if(sortedList2.get(i).getValue() > 10*60)
                                returnMessage += (i+1)+"등 - "+member.getName()+"\n -> "+sortedList2.get(i).getValue()/60+"시간 "+sortedList2.get(i).getValue()%60+"분\n";
                            else newMessage += (i+1)+" 등 - "+member.getName()+"\n -> "+0+" 시간 "+0+"분\n";
                        }
                    }
                    if(cnt2==0) newMessage += member.getName() + "님은 근무 시간 미달자가 아닙니다.\n";
                    else newMessage += member.getName() + "님은 근무 시간 미달자입니다.\n";

                    embed.setDescription(returnMessage +"\n"+ newMessage);
                }
                break;
        }
        event.replyEmbeds(embed.build()).setEphemeral(false).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        sendToSchedule(event);
    }

    public void sendToSchedule(@NotNull ReadyEvent readyEvent){
        List<Member> memberList = memberRepository.findAll();
        Map<String, Integer> memberMap = new HashMap<>();
        for(Member member : memberList){
            Pattern pattern = Pattern.compile("\\b(\\d+) hrs (\\d+) min(s)?\\b");
            Matcher matcher = pattern.matcher(member.getSevenDays());
            if(matcher.find()){
                int hour = Integer.parseInt(matcher.group(1));
                int minute = Integer.parseInt(matcher.group(2));
                memberMap.put(member.getName(),hour*60+minute);
            }
            else memberMap.put(member.getName(),0);
        }

        List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());
        Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
        String newMessage = "!!!!!***근무 시간 미달자 ***!!!! \n";
        String returnMessage = "";
        int cnt=0;
        for(int i=0;i<sortedList.size();i++){
            if(sortedList.get(i).getValue() <=10*60){
                cnt++;
                newMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n";
            }
            else if(sortedList.get(i).getValue() > 10*60)
                returnMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n";
            else newMessage += (i+1)+" 등 - "+sortedList.get(i).getKey()+"\n -> "+0+" 시간 "+0+"분\n";
        }
        if(cnt==0) newMessage += "현재 근무 시간 미달자가 없습니다.\n";
        String message = "현재 기준 순위입니다. \n"+returnMessage + "\n" + newMessage;

        // get the current ZonedDateTime of your TimeZone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        ZonedDateTime nextFriday = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        if (now.getDayOfWeek() == DayOfWeek.FRIDAY && now.isAfter(nextFriday)) {
            nextFriday = nextFriday.plusWeeks(1);
        }
        nextFriday = nextFriday.withHour(0).withMinute(0);

        // duration between now and the beginning of the next first lesson
        Duration durationUntil = Duration.between(now, nextFriday);
        long initialDelay =  durationUntil.getSeconds();

        ScheduledExecutorService schedulerFirstLesson = Executors.newScheduledThreadPool(1);
        schedulerFirstLesson.scheduleAtFixedRate(() -> {
                    readyEvent.getJDA().getTextChannelById("1090659127417638943").sendMessage("이번주 와카타임 랭킹입니다. !").setEmbeds(
                            new EmbedBuilder().addField("https://megabrain.kr/waka", "", false)
                                    .setColor(Color.green)
                                    .setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 개인 순위")
                                    .setThumbnail("https://avatars.githubusercontent.com/inje-megabrain")
                                    .setFooter("메가브레인 와카 봇", "https://avatars.githubusercontent.com/inje-megabrain")
                                    .setDescription(message)
                                    .build()
                    ).queue();
                },
                initialDelay,
                TimeUnit.DAYS.toSeconds(7),
                TimeUnit.SECONDS);
    }

}
