#!/bin/sh
/usr/bin/mc config host add myminio http://minio:9000 minioadmin minioadmin;
/usr/bin/mc mb -p myminio/literatureforum-assets;
/usr/bin/mc policy set download myminio/literatureforum-assets;
exit 0;