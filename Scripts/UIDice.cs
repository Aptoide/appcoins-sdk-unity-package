using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UIDice : MonoBehaviour
{
    [SerializeField]
    private GameObject[] _faces;
    private GameObject _2face;
    private GameObject _3face;
    private GameObject _4face;
    private GameObject _5face;
    private GameObject _6face;

    // Start is called before the first frame update
    void Awake()
    {
        TurnAllFacesOff();
    }

    private void TurnAllFacesOff()
    {
        foreach (GameObject face in _faces)
        {
            TurnObjectOff(face);
        }
    }

    private void TurnObjectOff(GameObject obj)
    {
        if (obj.activeSelf)
            obj.SetActive(false);
    }

    public void SetValue(int value)
    {
        if (value < 1 ||value > 6)
        {
            Debug.LogError($"Trying to show unexistent dice face: {value}");
            return;
        }

        TurnAllFacesOff();
        _faces[value - 1].SetActive(true);
    }
}
