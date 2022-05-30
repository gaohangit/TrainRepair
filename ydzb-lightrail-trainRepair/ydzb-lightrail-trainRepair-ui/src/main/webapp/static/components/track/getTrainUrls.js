const baseUrl =`${ctxPath}/static/components/track/img/train/`
function getTrainUrls(trainType){
  let urls = []
  for (let i = 1; i < 4; i++) {
    urls.push(`${baseUrl}${trainType}-${i}.png`)
  }
  return urls
}