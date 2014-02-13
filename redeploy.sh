cd $KB_TOP/modules/dev_container
git pull
cd ../modules/typecomp
git pull
cd ../modules/networks
git pull
cd ../modules/auth
git pull

cd ../
./bootstrap $KB_RUNTIME
source ./user-env.sh

make

cd modules/networks
make test &> networks_test.out_${DATE}_${TIME}
