
# ./spanner-generator.py num_spanners num_nuts num_locations
for i in `seq 1 20`
do
    span=$(( ( RANDOM % 30 )  + 1 ))
    nuts=$(( ( RANDOM % 30 )  + 1 ))
    loc=$(( ( RANDOM % 20 )  + 1 ))
    echo "./spanner-generator.py ${span} ${nuts} ${loc} p${i} ../problem/p${i}.pddl"
    ./spanner-generator.py ${span} ${nuts} ${loc} p${i} ../problem/p${i}.pddl
done
