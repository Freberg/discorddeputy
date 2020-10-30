(docker network create --driver bridge shared_net || true) &&
(cd discorddeputy-deploy/config/container/rabbitMQ || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/mongoDB || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/discorddeputy/bot || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/discorddeputy/api-epic || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/discorddeputy/api-steam || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/discorddeputy/fetcher-epic || exit && docker-compose down && docker-compose up -d) &&
(cd discorddeputy-deploy/config/container/discorddeputy/fetcher-steam || exit && docker-compose down && docker-compose up -d)