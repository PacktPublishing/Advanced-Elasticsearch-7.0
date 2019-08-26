#!/bin/bash
cd /usr/app/src
export PYTHONPATH=.:/usr/local/lib/python3.6/dist-packages/pyspark/jars:$PYTHONPATH
export PYSPARK_PYTHON=python3.6
export PYSPARK_DRIVER_PYTHON=python3.6
python3.6 com/example/spark/run.py
