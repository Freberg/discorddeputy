(cd discorddeputy-fetcher-epic || exit && .././gradlew bootBuildImage --imageName=discorddeputy/fetcher-epic)
(cd discorddeputy-api || exit && .././gradlew bootBuildImage --imageName=discorddeputy/api)
(cd discorddeputy-bot || exit && .././gradlew bootBuildImage --imageName=discorddeputy/bot)
