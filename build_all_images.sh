#! /bin/sh
platform=
tag=latest

build() {
  service=$1
  cd "discorddeputy-$service" && mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar) && \
    docker buildx build --platform "$platform" -t "discorddeputy/$service:$tag" .
  cd ..
}

while getopts 'p:t:' OPTION
do
  case ${OPTION} in
    p)
      platform="$OPTARG"
      ;;
    t)
      tag="$OPTARG"
      ;;
    *)
      exit 2
      ;;
  esac
done

if [ -z "${platform}" ]; then
  echo "Platform is not specified!"
  exit 2
fi

build "fetcher-epic"
build "fetcher-steam"
build "api-epic"
build "api-steam"
build "bot"


