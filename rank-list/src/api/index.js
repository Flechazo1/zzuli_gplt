import ajax from './ajax'

export const getRankHeader = () => ajax("/info/problems")
export const getRank = () => ajax("/icpc/rank")
export const getFirstAc = () => ajax("/info/firstAc")