cd /kb/dev_container/modules/dev_container
git pull
cd /kb/dev_container/modules/typecomp
git pull
cd /kb/dev_container/modules/networks
git pull
cd /kb/dev_container/modules/auth
git pull

cd /kb/dev_container/
./bootstrap /kb/runtime
source /kb/dev_container/user-env.sh

make

cd /kb/dev_container/modules/networks
make test &> networks_test.out_${DATE}_${TIME}
