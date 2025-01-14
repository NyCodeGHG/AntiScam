package tv.banko.antiscam.manage;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.WebhookExecuteSpec;
import tv.banko.antiscam.AntiScam;

import java.time.Instant;

public class Stats {

    private final AntiScam antiScam;
    private final Webhook webhook;

    public Stats(AntiScam antiScam) {
        this.antiScam = antiScam;

        webhook = this.antiScam.getGateway().getWebhookByIdWithToken(Snowflake.of(System.getenv("STATS_WEBHOOK_ID")),
                System.getenv("STATS_WEBHOOK_TOKEN")).blockOptional().orElse(null);
    }

    public void sendScam(Message message) {
        Member member = message.getAuthorAsMember().block();
        GuildMessageChannel channel = (GuildMessageChannel) message.getChannel().block();
        Guild guild = message.getGuild().block();

        assert member != null;
        assert channel != null;
        assert guild != null;
        webhook.execute(WebhookExecuteSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":newspaper: | Scam detected")
                        .description("**Sender**: **" + member.getTag() + "**\n" +
                                "**Channel**: **" + channel.getName() + "**\n" +
                                "**Guild**: **" + guild.getName() + "**\n")
                        .addField(EmbedCreateFields.Field.of("Message", message.getContent(), false))
                        .addField(EmbedCreateFields.Field.of("Timestamp", "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .addField(EmbedCreateFields.Field.of("IDs", "```ini" + "\n" +
                                "userId = " + member.getId().asString() + "\n" +
                                "channelId = " + channel.getId().asString() + "\n" +
                                "messageId = " + message.getId().asString() + "\n" +
                                "guildId = " + guild.getId().asString() + "\n" +
                                "```", false))
                        .build())
                .build()).onErrorStop().block();
    }

    public void sendNewPhrase(String phrase, Snowflake userId, Snowflake guildId) {
        User user = antiScam.getGateway().getUserById(userId).blockOptional().orElse(null);
        Guild guild = antiScam.getGateway().getGuildById(guildId).blockOptional().orElse(null);

        assert user != null;
        assert guild != null;
        webhook.execute(WebhookExecuteSpec.builder()
                .content("@everyone")
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":newspaper: | New phrase")
                        .description("**Applicant**: **" + user.getTag() + "**\n" +
                                "**Guild**: **" + guild.getName() + "**\n")
                        .addField(EmbedCreateFields.Field.of("Phrase", phrase, false))
                        .addField(EmbedCreateFields.Field.of("Approve", "`/antiscam approve " + phrase + "`", false))
                        .addField(EmbedCreateFields.Field.of("Timestamp", "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .addField(EmbedCreateFields.Field.of("IDs", "```ini" + "\n" +
                                "userId = " + user.getId().asString() + "\n" +
                                "guildId = " + guild.getId().asString() + "\n" +
                                "```", false))
                        .build())
                .build()).onErrorStop().block();
    }
}
