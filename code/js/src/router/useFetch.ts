import {
    useState,
    useEffect,
} from 'react'

export function useFetch<T>(url: string, options): T {
    const [response, setResponse] = useState<T | null>(null);
    useEffect(() => {
     async function fetchData() {
        try {
          const res = await fetch(url,options);
          const json = await res.json();
          setResponse(json);
        } catch (error) {
          // handle error
        }
      }
  
      fetchData();
    }, []);
    console.log(response)
    //@ts-ignore
    return response;
  }
