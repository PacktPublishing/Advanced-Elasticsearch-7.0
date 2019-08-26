from com.example.client.config.low_level_client_by_connection import ESLowLevelClientByConnection
from elasticsearch_dsl import Search
from elasticsearch_dsl.query import Q, Bool, Range, Term
from elasticsearch_dsl.aggs import A, DateHistogram, ScriptedMetric, MovingAvg, BucketScript


def bollinger_band(index='cf_etf_hist_price', start_date='2018-12-26', end_date='2019-03-25', symbol='rfem'):
    ESLowLevelClientByConnection.get_instance()
    search = Search(index=index, using='high_level_client')[0:0]
    search.query = Q(Bool(must=[Range(date={'gte': '2018-12-26', 'lte': '2019-03-25'}), Term(symbol='rfem')]))
    aggs = A(DateHistogram(field='date', interval='1d', format='yyyy-MM-dd', min_doc_count=1))
    aggs_tp = A(ScriptedMetric(init_script='state.totals=[]',
                map_script='state.totals.add((doc.high.value+doc.low.value+doc.close.value)/3)',
                combine_script='double total=0; for (t in state.totals) {total += t} return total',
                reduce_script='double total=0; for (t in states) {total += t} return total'))
    aggs_moving_avg = A(MovingAvg(model='simple', window=20, buckets_path='tp.value'))
    aggs_bbu = A(BucketScript(buckets_path={'SMA':'20_trading_days_moving_avg'}, script='params.SMA + 0.5'))
    aggs_bbl = A(BucketScript(buckets_path={'SMA': '20_trading_days_moving_avg'}, script='params.SMA - 0.5'))
    search.aggs.bucket('Bollinger_band', aggs).metric('tp', aggs_tp).pipeline(
        '20_trading_days_moving_avg', aggs_moving_avg).pipeline('BBU', aggs_bbu).pipeline('BBL', aggs_bbl)
    response = search.execute()
    print(response.to_dict())


if __name__ == "__main__":
    bollinger_band()
