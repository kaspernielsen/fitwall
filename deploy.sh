docker create \
  --name niord-reverseproxy \
  --restart=unless-stopped \
  -p 8000:8000 \
  dmadk/niord-wms-hotfix:1.0
  

docker create \
  --name niord-reverseproxy-test \
  --restart=unless-stopped \
  --link niord-reverseproxy:niord-reverseproxy \
  dmadk/niord-wms-hotfix-test:1.0

  
docker start niord-reverseproxy niord-reverseproxy-test