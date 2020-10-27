# DiscordDeputy

Discord Deputy is a Discord bot that provides steam news and free game offers into your discord channel

The application consists of multiple services:
* Fetcher services
    * Epic games offer service
    * Steam news fetcher service
* API services
    * Epic games offer APIC service
    * Steam news API service
* BOT service

Additional information:
* The services communicate via rabbitMQ
* The API services persists data in a mongoDB
    
