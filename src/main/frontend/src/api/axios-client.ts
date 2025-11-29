import axios, {type AxiosRequestConfig} from 'axios';

export const AXIOS_INSTANCE = axios.create();

export const customInstance = async <T>(config: AxiosRequestConfig, options?: AxiosRequestConfig): Promise<T> => {
    const source = axios.CancelToken.source();
    const {data} = await AXIOS_INSTANCE({
        ...config,
        ...options,
        cancelToken: source.token,
    });
    return data;
};