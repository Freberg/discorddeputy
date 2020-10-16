(cd discorddeputy-fetcher-epic || exit && gradle bootBuildImage --imageName=discorddeputy/fetcher-epic)
(cd discorddeputy-api || exit && gradle bootBuildImage --imageName=discorddeputy/api)
(cd discorddeputy-bot || exit && gradle bootBuildImage --imageName=discorddeputy/bot)
