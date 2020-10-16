(cd discorddeputy-deploy/config/container/discorddeputy/fetcher-epic || exit && docker-compose down) &&
(cd discorddeputy-deploy/config/container/discorddeputy/api || exit && docker-compose down) &&
(cd discorddeputy-deploy/config/container/discorddeputy/bot || exit && docker-compose down) &&
(cd discorddeputy-deploy/config/container/mongoDB || exit && docker-compose down) &&
(cd discorddeputy-deploy/config/container/rabbitMQ || exit && docker-compose down)
