package mega.waka.discord;

import jakarta.validation.constraints.NotNull;
import mega.waka.entity.Member;
import mega.waka.repository.MemberRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    public DiscordListener(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {  //test->1116650046797135992, 1090659127417638943
        try{
            if(!event.getChannel().getId().equals("1090659127417638943")) return;
            super.onSlashCommandInteraction(event);

            User user = event.getUser();

            Map<String, Integer> memberMap = returnToMemberTimeByMap();
            List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());

            StringBuilder messange;
            EmbedBuilder embed = setEmbed();
            switch(event.getName()){
                case "전체순위" :
                    messange = returnToOverallRanking(sortedList);
                    embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 전체 순위");
                    embed.setDescription(messange.toString());
                    memberMap.clear();
                    sortedList.clear();
                    break;
                case "개인순위" :
                    Member member = findMemberNameByDiscord_Id(user.getName());
                    messange = returnToPersonalRanking(sortedList,member);
                    embed.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 개인 순위");
                    embed.setDescription(messange.toString());
                    memberMap.clear();
                    sortedList.clear();
                    break;
            }
            event.replyEmbeds(embed.build()).setEphemeral(false).queue();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        sendToSchedule(event);
    }

    public void sendToSchedule(@NotNull ReadyEvent readyEvent){
        Map<String, Integer> memberMap = returnToMemberTimeByMap();
        List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(memberMap.entrySet());
        StringBuilder messange = returnToOverallRanking(sortedList);
        // in seconds
        long initialDelay = calculate_NextFriday_FromToday_ToSecond();

        ScheduledExecutorService schedulerFirstLesson = Executors.newScheduledThreadPool(1);
        schedulerFirstLesson.scheduleAtFixedRate(() -> {
                    readyEvent.getJDA().getTextChannelById("1090659127417638943").sendMessage("이번주 와카타임 랭킹입니다. !").setEmbeds(
                            new EmbedBuilder().addField("https://megabrain.kr/waka", "", false)
                                    .setColor(Color.green)
                                    .setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+" 기준 " +"와카보드 개인 순위")
                                    .setThumbnail("https://avatars.githubusercontent.com/inje-megabrain")
                                    .setFooter("메가브레인 와카 봇", "https://avatars.githubusercontent.com/inje-megabrain")
                                    .setDescription(messange.toString())
                                    .build()
                    ).queue();
                },
                initialDelay,
                TimeUnit.DAYS.toSeconds(7),
                TimeUnit.SECONDS);
    }
    public Map<String, Integer> returnToMemberTimeByMap (){
        Map<String, Integer> memberMap = new HashMap<>();
        List<Member> memberList = memberRepository.findAll();
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
                System.out.println(member.getName());
            }
        }
        return memberMap;
    }
    public Member findMemberNameByDiscord_Id (String discord_id){
        Member findMember;
        if(discord_id.equals("_ung_")) findMember = memberRepository.findByName("신종웅");
        else if(discord_id.equals("jadru"))  findMember = memberRepository.findByName("박영건");
        else if(discord_id.equals("haroya")) findMember = memberRepository.findByName("김동현");
        else if(discord_id.equals("LeeByeongJin")) findMember = memberRepository.findByName("이병진");
        else if(discord_id.equals("ddeltas")) findMember = memberRepository.findByName("구본웅");
        else if(discord_id.equals("jyh")) findMember = memberRepository.findByName("정용휘");
        else if(discord_id.equals("gimuhyeon")) findMember = memberRepository.findByName("김우현");
        else if(discord_id.equals("byeongseok")) findMember = memberRepository.findByName("성병석");
        else if(discord_id.equals("훈정이")) findMember = memberRepository.findByName("김훈정");
        else if(discord_id.equals("보경")) findMember = memberRepository.findByName("문보경");
        else if(discord_id.equals("Zn")) findMember = memberRepository.findByName("심아연");
        else if(discord_id.equals("Rod12")) findMember = memberRepository.findByName("이욱진");
        else if(discord_id.equals("재민")) findMember = memberRepository.findByName("김재민");
        else if(discord_id.equals("hongjihyeon")) findMember = memberRepository.findByName("홍지현");
        else{
            findMember = memberRepository.findByName(discord_id);
        }
        return findMember;
    }
    public StringBuilder returnToOverallRanking(List<Map.Entry<String,Integer>> sortedList){
        Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
        StringBuilder message = new StringBuilder();
        int cnt=0;
        for(int i=0;i<sortedList.size();i++){
            int totalMinutes = sortedList.get(i).getValue();
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            if (totalMinutes < 10 * 60) {
                if (totalMinutes > 0) {
                    ++cnt;
                    if(cnt==1) message.append("****** 근무시간 미달자 ******");
                    message.append((i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> " + hours + "시간 " + minutes + "분\n");
                } else {
                    message.append((i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> 0시간 0분\n");
                }
            } else {

                message.append((i + 1) + " 등 - " + sortedList.get(i).getKey() + "\n -> " + hours + "시간 " + minutes + "분\n");
            }
        }
        if(cnt==0) message.append("현재 근무 시간 미달자가 없습니다.\n");
        return message;
    }
    public StringBuilder returnToPersonalRanking(List<Map.Entry<String,Integer>> sortedList,Member member){
        Collections.sort(sortedList, Map.Entry.comparingByValue(Comparator.reverseOrder()));
        StringBuilder message = new StringBuilder();
        int cnt=0;
        for(int i=0;i<sortedList.size();i++){
            if(sortedList.get(i).getKey().equals(member.getName())){

                if(sortedList.get(i).getValue() <=10*60) {
                    cnt++;
                    message.append( i+1+" 등 - "+member.getName()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n");
                }
                else if(sortedList.get(i).getValue() > 10*60){
                    message.append((i+1)+"등 - "+member.getName()+"\n -> "+sortedList.get(i).getValue()/60+"시간 "+sortedList.get(i).getValue()%60+"분\n");
                }
                else  message.append((i+1)+" 등 - "+member.getName()+"\n -> "+0+" 시간 "+0+"분\n");
            }
        }
        if(cnt==0) message.append(member.getName() + "님은 근무 시간 미달자가 아닙니다.\n");
        else message.append(member.getName() + "님은 근무 시간 미달자입니다.\n");
        return message;
    }
    public EmbedBuilder setEmbed(){
        EmbedBuilder embed = new EmbedBuilder();
        embed.addField("https://megabrain.kr/waka", "", false);
        embed.setThumbnail("https://avatars.githubusercontent.com/inje-megabrain");
        embed.setFooter("메가브레인 와카 봇", "https://avatars.githubusercontent.com/inje-megabrain");
        embed.setColor(Color.green);
        return embed;
    }

    public long calculate_NextFriday_FromToday_ToSecond(){
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
        return durationUntil.getSeconds();
    }


}
