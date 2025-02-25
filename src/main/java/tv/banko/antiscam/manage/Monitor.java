package tv.banko.antiscam.manage;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Webhook;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.WebhookExecuteSpec;
import tv.banko.antiscam.AntiScam;

import java.time.Instant;

public class Monitor {

    private final AntiScam antiScam;
    private final Webhook webhook;

    public Monitor(AntiScam antiScam) {
        this.antiScam = antiScam;

        webhook = this.antiScam.getGateway().getWebhookByIdWithToken(Snowflake.of(System.getenv("MONITOR_WEBHOOK_ID")),
                System.getenv("MONITOR_WEBHOOK_TOKEN")).blockOptional().orElse(null);
    }

    public void sendOnline() {
        webhook.execute(WebhookExecuteSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":arrow_double_up: | Bot online")
                        .description("Bot is now **online**.")
                        .addField(EmbedCreateFields.Field.of("Timestamp",
                                "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .build())
                .build()).onErrorStop().block();
    }

    public void sendOffline() {
        webhook.execute(WebhookExecuteSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":arrow_double_down: | Bot offline")
                        .description("Bot is now **offline**.")
                        .addField(EmbedCreateFields.Field.of("Timestamp",
                                "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .build())
                .build()).onErrorStop().block();
    }

    public void sendGuildJoin(Guild guild) {
        webhook.execute(WebhookExecuteSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":heavy_plus_sign: | Bot added")
                        .description("Bot joined **" + guild.getName() + "**.")
                        .addField(EmbedCreateFields.Field.of("Timestamp",
                                "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .addField(EmbedCreateFields.Field.of("IDs", "```ini" + "\n" +
                                "guildId = " + guild.getId().asString() + "\n" +
                                "```", false))
                        .build())
                .build()).onErrorStop().block();
    }

    public void sendGuildLeave(Guild guild) {
        webhook.execute(WebhookExecuteSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":heavy_plus_sign: | Bot removed")
                        .description("Bot left **" + guild.getName() + "**.")
                        .addField(EmbedCreateFields.Field.of("Timestamp",
                                "<t:" + Instant.now().getEpochSecond() + ":f>", false))
                        .addField(EmbedCreateFields.Field.of("IDs", "```ini" + "\n" +
                                "guildId = " + guild.getId().asString() + "\n" +
                                "```", false))
                        .build())
                .build()).onErrorStop().block();
    }

    public void sendError(Throwable throwable) {
        webhook.execute(WebhookExecuteSpec.builder()
                .content("@everyone")
                .addEmbed(EmbedCreateSpec.builder()
                        .title(":warning: | Error")
                        .description("`" + throwable.getClass().getName() + "` occured!" +
                                "\n`" + throwable + "`")
                        .build())
                .build()).onErrorStop().block();
    }

}
