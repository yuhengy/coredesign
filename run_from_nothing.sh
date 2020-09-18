# Interested in 
# from **setup docker enviornment**, right throught run simulation, and clean up everything
# in one command?

# run this file!

# You only need docker and docker-compose(bounded with docker destop on Mac/Win)
# No need worry about enviornment of scala, verilator ...
# and everything will be removed after this run, i.e., this run will not pollute you machine

docker-compose up -d

# This enable non-interactive bash to `source /root/.bashrc`
docker-compose exec my_env bash -c "sed -i 's/\[\ -z\ \"\$PS1\"\ \]\ \&\&\ return/\#\ ThisLineIsRemoved/g' /root/.bashrc"

docker-compose exec my_env bash -c "source /root/.bashrc; sh ProjectRiscv/coredesign/test-scripts-forcommits/5-commited6a367.sh"

# This disable non-interactive bash to `source /root/.bashrc`
docker-compose exec my_env bash -c "sed -i 's/\#\ ThisLineIsRemoved/\[\ -z\ \"\$PS1\"\ \]\ \&\&\ return/g' /root/.bashrc"

docker-compose down --rmi all
