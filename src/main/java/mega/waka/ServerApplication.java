package mega.waka;

import mega.waka.discord.DiscordListener;
import mega.waka.repository.MemberRepository;
import mega.waka.service.SevenDaysWakaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ServerApplication {
	private final MemberRepository memberRepository;
	private final SevenDaysWakaService sevenDaysWakaService;
	@Value("${discord.bot.token}")
	private String token;
	@Autowired
	public ServerApplication(MemberRepository memberRepository, SevenDaysWakaService sevenDaysWakaService) {
		this.memberRepository = memberRepository;
		this.sevenDaysWakaService = sevenDaysWakaService;
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerApplication.class, args);
		ServerApplication serverApplication = context.getBean(ServerApplication.class);
		serverApplication.startBot();
	}
	/*@Component
	class DiscordToken {
		@Value("${discord.bot.token}")
		private String token;
		public String getToken() {
			return token;
		}
	}*/
	public void startBot() {
		sevenDaysWakaService.update_SevenDays();
		JDA jda = JDABuilder.createDefault(token)
				.setActivity(Activity.playing("코딩"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new DiscordListener(memberRepository, sevenDaysWakaService))
				.build();
		jda.upsertCommand("개인순위","금주 와카타임 개인순위를 제공합니다.").setGuildOnly(true).queue();
		jda.upsertCommand("전체순위","금주 와카타임 전체순위를 제공합니다.").setGuildOnly(true).queue();
		jda.upsertCommand("ping","pong").setGuildOnly(true).queue();
		// 나머지 코드
	}

}
