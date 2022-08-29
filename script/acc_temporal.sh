for domain in peg zenotravel
do
    python3 script/temporal/amlsi_acc.py $domain experiment/temporal/ seq
done


for domain in match
do
    python3 script/temporal/amlsi_acc.py $domain experiment/temporal/ she
done

