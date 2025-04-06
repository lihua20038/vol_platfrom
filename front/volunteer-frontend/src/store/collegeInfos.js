import {
    defineStore
} from 'pinia'
import {
    ref
} from 'vue'
import {
    getAllColleges
} from '@/api/college.js';

export const useCollegeInfosStore = defineStore('collegeInfos', () => {
    const colleges = ref([])

    const setColleges = (newInfo) => {
        colleges.value = newInfo
    }

    const remove = () => {
        colleges.value = []
    }

    const fetchColleges = async () => {
        await getAllColleges()
            .then((res) => {
                setColleges(res?.data);
            })
            .catch((err) => {});
    }

    const isEmpty = () => {
        return !colleges.value.length;
    }

    return {
        colleges,
        setColleges,
        remove,
        fetchColleges,
        isEmpty
    }

}, {
    persist: true
})

// export default useUserInfoStore;